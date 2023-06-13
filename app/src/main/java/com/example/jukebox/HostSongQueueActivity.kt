package com.example.jukebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.jukebox.ui.theme.JukeboxTheme

class HostSongQueueActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
    }
}