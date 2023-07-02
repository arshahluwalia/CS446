package com.example.jukebox.songqueue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jukebox.SecondaryBackground
import com.example.jukebox.Song
import com.example.jukebox.roomManager
import com.example.jukebox.ui.theme.JukeboxTheme
import kotlinx.coroutines.flow.MutableStateFlow

private lateinit var roomCode : String
class GuestSongQueueActivity  : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomCode = intent.getStringExtra("roomCode").toString()
        val songQueue = MutableStateFlow<List<Song>>(emptyList())
        getSongQueue(roomCode, songQueue)
        val hostName = MutableStateFlow("")
        getHostName(roomCode, hostName)
        setContent {
            // TODO: need to retrieve song list, and current song instead of hardcoding
            JukeboxTheme() {
                SongQueueScreenContent(
                    hostName = hostName.collectAsState().value,
                    isHost = false,
                    playingSong = Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = true),
                    queuedSongList = songQueue.collectAsState().value,
                    roomCode = roomCode
                )
            }
        }
    }

    private fun getSongQueue(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        roomManager.getQueue(roomCode) { queue ->
            songQueue.value = queue.queue
        }
    }

    private fun getHostName(roomCode: String, hostName: MutableStateFlow<String>) {
        roomManager.getHostName(roomCode) { name ->
            hostName.value = name
        }
    }
}

@Preview
@Composable
private fun PreviewScreenContent() {
    JukeboxTheme() {
        SecondaryBackground()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SongQueueScreenContent(
                hostName = "Lucas",
                isHost = false,
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
                ),
                roomCode = "ABCDE"
            )
        }
    }
}