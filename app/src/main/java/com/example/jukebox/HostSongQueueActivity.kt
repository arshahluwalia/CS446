package com.example.jukebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jukebox.ui.theme.JukeboxTheme

class HostSongQueueActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "entername") {
                composable("entername") { EnterName(navController) }
                composable("songqueue") { SongQueue() }
            }
        }
    }
}

@Composable
private fun EnterName(navController: NavController) {
    JukeboxTheme() {

    }
}
@Composable
private fun SongQueue() {
    JukeboxTheme() {
        SongQueueScreenContent(
            hostName = "Lucas",
            isHost = true,
            playingSong = Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = true),
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