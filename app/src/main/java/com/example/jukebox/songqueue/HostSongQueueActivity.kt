package com.example.jukebox.songqueue

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jukebox.R
import com.example.jukebox.RoomManager
import com.example.jukebox.SecondaryBackground
import com.example.jukebox.Song
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.util.HideSoftKeyboard
import kotlinx.coroutines.flow.MutableStateFlow

private lateinit var roomCode : String

class HostSongQueueActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val songQueue = MutableStateFlow<List<Song>>(emptyList())
        roomCode = intent.getStringExtra("roomCode").toString()
        getSongQueue(roomCode, songQueue)
        val roomManager = RoomManager()
        val appContext = applicationContext
        val dispatcher = onBackPressedDispatcher
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "entername") {
                composable("entername") { EnterName(navController) }
                composable(
                    "songqueue/{hostName}",
                    arguments = listOf(navArgument("hostName") { type = NavType.StringType })
                ) {backStackEntry ->
                    SongQueue(
                        dispatcher = dispatcher,
                        backStackEntry.arguments?.getString("hostName"),
                        songQueue = songQueue,
                        removeSong = ::removeSong,
                        roomManager = roomManager,
                        appContext = appContext
                    )
                }
            }
        }
    }

    private fun getSongQueue(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        val roomManager = RoomManager()
        roomManager.getQueue(roomCode) { queue ->
            songQueue.value = queue.queue
        }
    }

    private fun removeSong(song: Song) {
        val roomManager = RoomManager()
        roomManager.removeSongFromQueue(roomCode, song.context_uri)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnterName(navController: NavController) {
    var hostName by remember { mutableStateOf("") }
    val roomManager = RoomManager()
    val activity = LocalContext.current as Activity
    JukeboxTheme {
        Box {
            SecondaryBackground()
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Enter your name here! This is what the guests will see.",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                )
                TextField(
                    modifier = Modifier.padding(vertical = 20.dp),
                    value = hostName,
                    onValueChange = {
                        hostName = it
                    },
                    label = {
                        Text(
                            text = "Enter your name",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    shape = RoundedCornerShape(20),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            HideSoftKeyboard.hideSoftKeyboard(activity = activity)
                            navController.navigate("songqueue/$hostName")
                            roomManager.setHostName(roomCode, hostName)
                        }
                    )
                )
                Button(
                    onClick = {
                        HideSoftKeyboard.hideSoftKeyboard(activity = activity)
                        navController.navigate("songqueue/$hostName")
                        roomManager.setHostName(roomCode, hostName)
                    },
                    enabled = hostName.isNotEmpty()
                ) {
                    Text(text = "Done")
                }
            }
        }
    }
}
@Composable
private fun SongQueue(
    dispatcher: OnBackPressedDispatcher? = null,
    hostName: String?,
    songQueue: MutableStateFlow<List<Song>>,
    removeSong: (Song) -> Unit = { },
    roomManager: RoomManager,
    appContext: Context
) {
    JukeboxTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)) {
            Image(painter = painterResource(id = R.drawable.secondary_background), contentDescription = null)
            SongQueueScreenContent(
                dispatcher = dispatcher,
                hostName = hostName ?: "You",
                isHost = true,
                playingSong = Song(
                    songTitle = "Hips Don't Lie",
                    songArtist = "Shakira",
                    isApproved = true
                ),
                queuedSongList = songQueue.collectAsState().value,
                roomCode = roomCode,
                removeSong = removeSong,
                roomManager = roomManager,
                appContext = appContext
            )
        }
    }
}

@Preview
@Composable
private fun PreviewScreenContent() {
    JukeboxTheme {
        SecondaryBackground()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SongQueueScreenContent(
                hostName = "Lucas",
                isHost = true,
                playingSong = Song(songTitle = "Hips Don't Lieeee", songArtist = "Shakira", isApproved = true),
                queuedSongList = listOf(
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = true),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                ),
                roomCode = "ABCDE",
                roomManager = null,
                appContext = LocalContext.current
            )
        }
    }
}