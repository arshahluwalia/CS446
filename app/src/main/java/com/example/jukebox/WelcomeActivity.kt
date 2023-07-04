package com.example.jukebox

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.songqueue.HostSongQueueActivity
import com.example.jukebox.spotify.task.SpotifyAccessTokenTask.requestAccessToken
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.ui.theme.LightPurple
import kotlin.random.Random


class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomManager = RoomManager()
        setContent {
            JukeboxTheme {
                ScreenContent(roomManager)
            }
        }
    }
}

@Composable
private fun ScreenContent(
    roomManager: RoomManager?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        PrimaryBackground()
        JukeBoxTitle()
        RoomManagement(roomManager = roomManager)
    }
}

@Composable
private fun JukeBoxTitle() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "JukeBox",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Stop looking for the party aux",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
private fun RoomManagement(roomManager: RoomManager?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(bottom = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        RoomCodeTextField(roomManager)
        StartARoomButton(roomManager)
        if (RoomStore.hasRecentRoom().collectAsState().value) {
            RoomStore.getMostRecentRoom()?.roomCode?.let { ReturnToRoomButton(it) }
        }
    }
}

@Composable
private fun RoomCodeTextField(
    roomManager: RoomManager?
) {
    var roomCode by remember { mutableStateOf("") }
    val isError by remember { mutableStateOf(false) }
    val errorText by remember { mutableStateOf("") }
    val charLimit = 5

    Row(
        modifier = Modifier.padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val context = LocalContext.current

        TextField(
            value = roomCode,
            onValueChange = {
                if (it.length <= charLimit) {
                    roomCode = it // TODO: need to handle input
                }
            },
            isError = isError,
            supportingText = {
                if (isError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = errorText,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            label = {
                Text(
                    text = "Enter your room code",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            shape = RoundedCornerShape(20),
            singleLine = true,
            trailingIcon = { QRCode() },
            modifier = Modifier.onKeyEvent {
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                    roomManager?.checkRoomExists(roomCode) { exists ->
                        if (exists) {
                            Log.d("Welcome Activity", "User joining room $roomCode")
                            val intent = Intent(context, AuthorizeActivity::class.java)
                            intent.putExtra("roomCode", roomCode)
                            intent.putExtra("isHost", false)
                            context.startActivity(intent)
                        } else {
                            AlertDialog.Builder(context)
                                .setTitle("Invalid Room Code")
                                .setMessage("No room exists with this code")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
                false
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    roomManager?.checkRoomExists(roomCode) { exists ->
                        if (exists) {
                            Log.d("Welcome Activity", "User joining room $roomCode")
                            requestAccessToken()
                            val intent = Intent(context, AuthorizeActivity::class.java)
                            intent.putExtra("roomCode", roomCode)
                            intent.putExtra("isHost", false)
                            context.startActivity(intent)
                        } else {
                            AlertDialog.Builder(context)
                                .setTitle("Invalid Room Code")
                                .setMessage("No room exists with this code")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            )
        )
    }
}

@Composable
private fun QRCode() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .clickable {
                val intent = Intent(context, QRActivity::class.java)
                context.startActivity(intent)
            }
            .size(30.dp)
    ) {
        Image(
            painterResource(id = R.drawable.qr_icon),
            contentDescription = "QR Icon",
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        )
    }
}

@Composable
private fun StartARoomButton(
    roomManager: RoomManager?
) {
    val context = LocalContext.current
    Button(
        shape = RoundedCornerShape(20),
        onClick = {
            val roomCode = generateRoomCode()
            roomManager?.createRoom(roomCode)
            requestAccessToken()
            val intent = Intent(context, AuthorizeActivity::class.java)
            intent.putExtra("roomCode", roomCode)
            intent.putExtra("isHost", true)
            context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(containerColor = LightPurple)
    ) {
        Text(
            text = AnnotatedString("Start a Room"),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun ReturnToRoomButton(roomCode: String) {
    val context = LocalContext.current
    Button(
        shape = RoundedCornerShape(20),
        onClick = {
            requestAccessToken()
            val intent = Intent(context, HostSongQueueActivity::class.java)
            intent.putExtra("roomCode", roomCode)
            intent.putExtra("isReturning", true)
            context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(containerColor = LightPurple)
    ) {
        Text(
            text = AnnotatedString("Return to Room"),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
@Preview
private fun PreviewScreenContent() {
    JukeboxTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            ScreenContent(roomManager = null)
        }
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

private fun testRoomManager() {
    val roomManager = RoomManager()
    val roomCode = generateRoomCode()
    val room = Room(roomCode)
    roomManager.createRoom(roomCode, room)
    roomManager.addUserTokenToRoom(roomCode, "newUserToken")
    roomManager.addUserTokenToRoom(roomCode, "newUserToken1")
    roomManager.addUserTokenToRoom(roomCode, "newUserToken2")
    roomManager.addUserTokenToRoom(roomCode, "newUserToken23")
    roomManager.removeUserFromRoom(roomCode, "newUserToken2")
    roomManager.addSongToQueue(roomCode, Song("testSong"))
    roomManager.addSongToQueue(roomCode, Song("testSong2"))
    roomManager.addSongToQueue(roomCode, Song("testSong3"))
    roomManager.removeSongFromQueue(roomCode, "testSong2")
    roomManager.removeSongFromQueue(roomCode, "testSong2")
    roomManager.upvoteSong(roomCode, "testSong3")
    roomManager.upvoteSong(roomCode, "testSong3")
    roomManager.upvoteSong(roomCode, "testSong3")
    roomManager.getUserTokens(roomCode) {userTokens ->
          if (userTokens.isNotEmpty()) {
                for (token in userTokens) {
                    Log.d("Room Manager", "Fetched user token: $token")
                }
          }
    }
    roomManager.getQueue(roomCode) {queue ->
        if (!queue.checkEmpty()) {
            for (song in queue.queue) {
                Log.d("Room Manager", "Fetched song: context_uri: ${song.context_uri}, " +
                        "Title: ${song.songTitle}, Artist: ${song.songArtist}, " +
                        "approved: ${song.approvalStatus}, votes: ${song.votes}")
            }
        }
    }
}
