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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.spotify.SpotifyAccessTokenTask.requestAccessToken
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.ui.theme.LightPurple
import com.example.jukebox.ui.theme.OffBlack
import com.example.jukebox.ui.theme.PurpleNeon
import kotlin.random.Random


class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JukeboxTheme() {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ScreenContent()
                }
            }
        }
    }
}

val roomManager = RoomManager()

@Composable
private fun ScreenContent() {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        PrimaryBackground()
        JukeBoxTitle()
        RoomCodeTextField()
        StartARoomButton()
    }
}

@Composable
fun BoxWithConstraintsScope.JukeBoxTitle() {
    Column(
        modifier = Modifier
            .padding(top = maxHeight / 4)
            .align(Alignment.TopCenter),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxWithConstraintsScope.RoomCodeTextField() {
    var roomCode by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val charLimit = 5

    fun validate(text: String) {
        roomManager.checkRoomExists(text) { exists ->
            isError = !((roomCode.length == charLimit) && exists)
        }
    }

    Row(
        modifier = Modifier
            .padding(bottom = maxHeight / 6)
            .align(Alignment.BottomCenter),
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
                        text = "Room code must be $charLimit characters",
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
                    validate(roomCode)
                    roomManager.checkRoomExists(roomCode) { exists ->
                        if (roomCode.length == charLimit) {
                            if (exists) {
                                Log.d("Welcome Activity", "User joined room $roomCode")
                                val intent = Intent(context, GuestSongQueueActivity::class.java)
                                context.startActivity(intent)
                            } else {
                                AlertDialog.Builder(context)
                                    .setTitle("Invalid Room Code")
                                    .setMessage("No room exists with this code")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        } else {
                            AlertDialog.Builder(context)
                                .setTitle("Invalid Room Code")
                                .setMessage("The Room Code Must be 5 Characters")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
                false
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    validate(roomCode)
                    roomManager.checkRoomExists(roomCode) { exists ->
                        if (roomCode.length == charLimit) {
                            if (exists) {
                                Log.d("Welcome Activity", "User joined room $roomCode")
                                val intent = Intent(context, GuestSongQueueActivity::class.java)
                                context.startActivity(intent)
                            } else {
                                AlertDialog.Builder(context)
                                    .setTitle("Invalid Room Code")
                                    .setMessage("No room exists with this code")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        } else {
                            AlertDialog.Builder(context)
                                .setTitle("Invalid Room Code")
                                .setMessage("The Room Code Must be 5 Characters")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            )
        )
    }
}

@Composable
fun QRCode() {
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
fun BoxWithConstraintsScope.StartARoomButton() {
    val context = LocalContext.current
    Button(
        modifier = Modifier
            .padding(bottom = maxHeight / 12)
            .align(Alignment.BottomCenter),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        shape = RoundedCornerShape(20),
        onClick = {
            val roomCode = generateRoomCode()
            roomManager.createRoom(roomCode)
            requestAccessToken()
            val intent = Intent(context, AuthorizeActivity::class.java)
            intent.putExtra("roomCode", roomCode)
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
@Preview
private fun PreviewScreenContent() {
    JukeboxTheme() {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            ScreenContent()
        }
    }
}

private fun generateRoomCode(): String {
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
          if (!userTokens.isEmpty()) {
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
                        "approved: ${song.isApproved}, votes: ${song.votes}")
            }
        }
    }
}
