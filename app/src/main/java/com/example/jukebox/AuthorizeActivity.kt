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
import androidx.compose.ui.unit.dp
import com.example.jukebox.songqueue.GuestSongQueueActivity
import com.example.jukebox.songqueue.HostSongQueueActivity
import com.example.jukebox.spotify.SpotifyUserToken
import com.example.jukebox.spotify.task.SpotifyAccessTokenTask
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.ui.theme.PurpleNeon
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class AuthorizeActivity : ComponentActivity() {

    private val clientID = BuildConfig.SPOTIFY_CLIENT_ID
    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private val requestCode = 1335 // Could be any we choose
    private val redirectUri = "jukebox://callback" //the one we registered with Spotify
    private var userAccessToken = ""

    private var showSpotifyButton by mutableStateOf(true)
    private var roomCode = MutableStateFlow("")
    private var isHost = false
    private val roomManager = RoomManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dispatcher = onBackPressedDispatcher
        val roomManager = RoomManager()
        isHost = intent.getBooleanExtra("isHost", false) //defaults to false if not passed
        if (!isHost) {
            roomCode.value = intent.getStringExtra("roomCode").toString()
        }
        setContent {
            JukeboxTheme {
                ScreenContent(
                    dispatcher = dispatcher,
                    showSpotifyButton = showSpotifyButton,
                    onRequestTokenClicked = { onRequestTokenClicked() },
                    isHost = isHost,
                    roomManager = roomManager,
                    roomCode = roomCode
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
            .setScopes(arrayOf("user-read-playback-state", "user-modify-playback-state",
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
                        roomManager.setHostToken(roomCode.value, userAccessToken)
                    } else{
                        roomManager.addUserToRoom(roomCode.value, User(userAccessToken))
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
    onRequestTokenClicked: () -> Unit,
    isHost: Boolean,
    roomManager: RoomManager?,
    roomCode: MutableStateFlow<String>
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
            RoleText(isHost)
            if (showSpotifyButton) {
                AuthorizeSpotifyButton(onRequestTokenClicked)
            } else {
                ContinueButton(isHost, roomManager, roomCode)
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
private fun RoleText(isHost: Boolean) {
    Text(
        text = if (isHost) "Creating a room as a host" else "Entering a room as a guest",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White
    )
}

@Composable
private fun AuthorizeSpotifyButton(onRequestTokenClicked: () -> Unit){
    Button(
        modifier = Modifier.padding(vertical = 30.dp),
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
private fun ContinueButton(
    isHost: Boolean,
    roomManager: RoomManager?,
    roomCode: MutableStateFlow<String>
) {
    val context = LocalContext.current
    Button(
        modifier = Modifier.padding(vertical = 30.dp),
        onClick = {
            SpotifyAccessTokenTask.requestAccessToken()
            if(isHost){
                val generatedRoomCode = generateRoomCode()
                roomCode.value = generatedRoomCode
                roomManager?.createRoom(generatedRoomCode)
                roomManager?.setHostToken(generatedRoomCode, SpotifyUserToken.getToken())
                val intent = Intent(context, HostSongQueueActivity::class.java)
                intent.putExtra("roomCode", generatedRoomCode)
                context.startActivity(intent)
            } else {
                roomManager?.addUserToRoom(roomCode.value, User(SpotifyUserToken.getToken()))
                val intent = Intent(context, GuestSongQueueActivity::class.java)
                intent.putExtra("roomCode", roomCode.value)
                context.startActivity(intent)
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon)
    ) {
        Text(
            text = AnnotatedString("Next"),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

private fun generateRoomCode(): String {
    val roomManager = RoomManager()
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') // Define the allowed characters
    var newRoomCode = (1..5)
        .map { allowedChars[Random.nextInt(allowedChars.size)] }
        .joinToString("")

    roomManager.checkRoomExists(newRoomCode) { exists ->
        if (exists) {
            // Unique room code generated
            Log.d("Room Manager", "Room Code Collision: $newRoomCode")
            newRoomCode = generateRoomCode()
        }
    }

    return newRoomCode
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
