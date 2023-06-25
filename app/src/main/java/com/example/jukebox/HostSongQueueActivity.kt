package com.example.jukebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jukebox.ui.theme.JukeboxTheme

class HostSongQueueActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            roomCode = intent.getStringExtra("roomCode").toString()
            val navController = rememberNavController()
            NavHost(navController, startDestination = "entername") {
                composable("entername") { EnterName(navController) }
                composable(
                    "songqueue/{hostName}",
                    arguments = listOf(navArgument("hostName") { type = NavType.StringType })
                ) {backStackEntry ->
                    SongQueue(backStackEntry.arguments?.getString("hostName"))
                }
            }
        }
    }
}

private lateinit var roomCode : String

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnterName(navController: NavController) {
    var hostName by remember { mutableStateOf("") }
    JukeboxTheme() {
        Box(modifier = Modifier) {
            Image(
                modifier = Modifier.scale(2.0f).background(color = Color.Black).fillMaxSize(),
                painter = painterResource(id = R.drawable.secondary_background),
                contentDescription = null
            )
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
                    )
                )
                Button(
                    onClick = {
                        navController.navigate("songqueue/$hostName")
                        roomManager.setHostName(roomCode, hostName)},
                    enabled = hostName.isNotEmpty()
                ) {
                    Text(text = "Done")
                }
            }
        }
    }
}
@Composable
private fun SongQueue(hostName: String?) {
    JukeboxTheme() {
        Box(modifier = Modifier.fillMaxSize().background(color = Color.Black)) {
            Image(painter = painterResource(id = R.drawable.secondary_background), contentDescription = null)
            SongQueueScreenContent(
                hostName = hostName ?: "You",
                isHost = true,
                playingSong = Song(
                    songTitle = "Hips Don't Lie",
                    songArtist = "Shakira",
                    isApproved = true
                ),
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
                )
            )
        }
    }
}