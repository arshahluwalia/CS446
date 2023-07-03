package com.example.jukebox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.songqueue.GuestSongQueueActivity
import com.example.jukebox.songqueue.HostSongQueueActivity
import com.example.jukebox.spotify.SpotifyUserToken
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.ui.theme.PurpleNeon
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlin.reflect.KClass

class AuthorizeActivity : ComponentActivity() {

    private val clientID = BuildConfig.SPOTIFY_CLIENT_ID;
    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private val requestCode = 1335; // Could be any we choose
    private val redirectUri = "jukebox://callback"; //the one we registered with Spotify
    private var userAccessToken = ""

    private var showSpotifyButton by mutableStateOf(true)
    private lateinit var roomCode : String
    private var isHost = false
    private val roomManager = RoomManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dispatcher = onBackPressedDispatcher
        setContent {
            JukeboxTheme() {
                roomCode = intent.getStringExtra("roomCode").toString()
                isHost = intent.getBooleanExtra("isHost", false) //defaults to false if not passed
                Log.d("Authorization", "roomCode: $roomCode")
                ScreenContent(
                    dispatcher = dispatcher,
                    showSpotifyButton = showSpotifyButton,
                    roomCode = roomCode,
                    onRequestTokenClicked = { onRequestTokenClicked() },
                    isHost = isHost
                )
            }
        }
    }

    private fun onRequestTokenClicked() {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN)
        AuthorizationClient.openLoginActivity(this, requestCode, request)
    }

    private fun getAuthenticationRequest(type: AuthorizationResponse.Type): AuthorizationRequest? {
        return AuthorizationRequest.Builder(clientID, type, getRedirectUri().toString())
            .setShowDialog(false)
            .setScopes(arrayOf<String>("user-read-playback-state", "user-modify-playback-state",
                "user-read-currently-playing",
                "app-remote-control",
                "streaming",
                "playlist-modify-public"))
            .setCampaign("your-campaign-token")
            .build()
    }

    private fun getRedirectUri(): Uri? {
        return Uri.parse(redirectUri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        Log.d("Authorization", "requestCode: $requestCode, resultCode: $resultCode")

        // Check if result comes from the correct activity
        if (requestCode == this.requestCode) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    Log.d("Authorization", "token: ${response.accessToken}")
                    showSpotifyButton = false
                    userAccessToken = response.accessToken
                    SpotifyUserToken.setToken(userAccessToken)
                    if(isHost) {
                        roomManager.setHostToken(roomCode, userAccessToken)
                    } else{
                        roomManager.addUserTokenToRoom(roomCode, userAccessToken)
                    }
                    // TODO: open song queue screen
                }
                AuthorizationResponse.Type.ERROR -> { onRequestTokenClicked() }
                else -> {}
            }
        }
    }
}

@Composable
private fun ScreenContent(
    dispatcher: OnBackPressedDispatcher? = null,
    showSpotifyButton: Boolean,
    roomCode: String,
    onRequestTokenClicked: () -> Unit,
    isHost: Boolean
) {
    Box {
        SecondaryBackground()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                BackButton(dispatcher)
            }
            AuthorizeTitle()
            roleText(isHost)
            ContinueButton(roomCode, isHost)
            if (showSpotifyButton) {
                AuthorizeSpotifyButton(onRequestTokenClicked)
            }
        }
    }
}

@Composable
private fun BackButton(dispatcher: OnBackPressedDispatcher? = null) {
    TextButton(
        onClick = { dispatcher?.onBackPressed() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.padding(end = 10.dp),
                painter = painterResource(
                    id = R.drawable.arrow_back
                ),
                contentDescription = null
            )
            Text(
                text = "Back",
                color = Color.White,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
private fun AuthorizeTitle() {
    Text(
        text = "Login to Spotify",
        style = MaterialTheme.typography.titleSmall,
        color = Color.White,
        modifier = Modifier.padding(top = 200.dp)
    )
}

@Composable
private fun roleText(isHost: Boolean) {
    Text(
        text = if (isHost) "you're creating a room as a host" else "you're entering a room as a guest",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White
    )
}

@Composable
private fun AuthorizeSpotifyButton(onRequestTokenClicked: () -> Unit){
    Button(
        modifier = Modifier.padding(bottom = 20.dp),
        shape = RoundedCornerShape(20),
        onClick = { onRequestTokenClicked() },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
    ) {
        Text(
            text = AnnotatedString("Login |"),
            style = MaterialTheme.typography.headlineSmall
        )
        Image(
            modifier = Modifier
                .padding(start = 5.dp)
                .width(60.dp),
            painter = painterResource(id = R.drawable.spotify_logo),
            contentDescription = null
        )
    }
}
@Composable
private fun ContinueButton(roomCode: String, isHost: Boolean) {
    val context = LocalContext.current
    Button(
        modifier = Modifier.padding(vertical = 30.dp),
        onClick = {
            if(isHost){
                val intent = Intent(context, HostSongQueueActivity::class.java)
                intent.putExtra("roomCode", roomCode)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, GuestSongQueueActivity::class.java)
                intent.putExtra("roomCode", roomCode)
                context.startActivity(intent)
            }

        },
        colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon)
    ) {
        Text(
            text = AnnotatedString("Go to queue"),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

/*
@Composable
@Preview
private fun PreviewScreenContent() {
    JukeboxTheme() {
        ScreenContent(
            showSpotifyButton = true,
            roomCode = "ABCDE",
            onRequestTokenClicked = { }
        )
    }
}*/
