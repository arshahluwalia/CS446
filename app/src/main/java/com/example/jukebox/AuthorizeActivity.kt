package com.example.jukebox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.spotify.SpotifyAccessTokenTask
import com.example.jukebox.spotify.SpotifyUserToken
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.ui.theme.PurpleNeon
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse


class AuthorizeActivity : ComponentActivity() {

    private val clientID = BuildConfig.SPOTIFY_CLIENT_ID;
    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private val requestCode = 1335; // Could be any we choose
    private val redirectUri = "jukebox://callback"; //the one we registered with Spotify
    private var userAccessToken = ""

    private var showSpotifyButton by mutableStateOf(true)
    private lateinit var roomCode : String
    private val roomManager = RoomManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JukeboxTheme() {
                roomCode = intent.getStringExtra("roomCode").toString()
                Log.d("Authorization", "roomCode: $roomCode")
                ScreenContent(
                    showSpotifyButton = showSpotifyButton,
                    roomCode = roomCode,
                    onRequestTokenClicked = { onRequestTokenClicked() }
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
                    roomManager.setHostToken(roomCode, userAccessToken)
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
    showSpotifyButton: Boolean,
    roomCode: String,
    onRequestTokenClicked: () -> Unit
) {
    Box {
        SecondaryBackground()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AuthorizeTitle()
            ContinueButton(roomCode)
            if (showSpotifyButton) {
                AuthorizeSpotifyButton(onRequestTokenClicked)
            }
        }
    }
}

@Composable
private fun AuthorizeTitle() {
    Text(text = "Login to Spotify", style = MaterialTheme.typography.titleSmall, color = Color.White)
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
private fun ContinueButton(roomCode: String) {
    val context = LocalContext.current
    Button(
        modifier = Modifier.padding(vertical = 30.dp),
        onClick = {
            SpotifyAccessTokenTask.requestAccessToken()
            val intent = Intent(context, HostSongQueueActivity::class.java)
            intent.putExtra("roomCode", roomCode)
            context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon)
    ) {
        Text(
            text = AnnotatedString("Go to host queue"),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

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
}