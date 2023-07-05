package com.example.jukebox.songqueue

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.jukebox.R
import com.example.jukebox.RoomManager
import com.example.jukebox.spotify.task.SpotifySongControlTask.getPlaybackState
import com.example.jukebox.spotify.task.SpotifySongControlTask.pauseSong
import com.example.jukebox.spotify.task.SpotifySongControlTask.playPreviousSong
import com.example.jukebox.spotify.task.SpotifySongControlTask.playSong
import com.example.jukebox.spotify.task.SpotifySongControlTask.resumeSong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun SongControl(
    hostToken: MutableStateFlow<String>,
    userTokens: MutableStateFlow<MutableList<String>>,
    roomCode: String
) {
    val scope = rememberCoroutineScope()
    val hToken = hostToken.collectAsState().value
    val uTokens = userTokens.collectAsState().value
    var isPaused by remember { mutableStateOf(false) }
    val roomManager = RoomManager()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                    Log.d("spotify control: token list", uTokens.toString())
                    Log.d("spotify control: host token", hToken)
                    scope.launch {
                        val prevSong = runBlocking { roomManager.getPrevSong(roomCode) }
                        if (prevSong != null) {
                            Log.d("SongControl", "prev Song: ${prevSong.context_uri}")
                        } else {
                            Log.d("SongControl", "prev Song is null")
                        }
                        runBlocking { roomManager.moveBackSong(roomCode) }
                        if (prevSong != null) {
                            playSong(
                                prevSong.context_uri,
                                0,
                                uTokens
                            )
                        }
                    }
            }
        ) {
            Image(painter = painterResource(id = R.drawable.previous_track), contentDescription = null)
        }
        Button(
            onClick = {
                scope.launch {
                    if (isPaused) {
                        resumeSong(uTokens)
                        isPaused = false
                    } else {
                        pauseSong(uTokens)
                        isPaused = true
                    }
                }
            }
        ) {
            Image(painter = painterResource(id = R.drawable.pause_track), contentDescription = null)
        }
        Button(
            onClick = {
                //TODO: https://api.spotify.com/v1/me/player/next
                Log.d("spotify fetch state: host token", hToken)
                scope.launch {
                    val nextSong = runBlocking {
                        roomManager.getNextSong(roomCode)
                    }
                    if (nextSong != null) {
                        Log.d("SongControl", "next Song: ${nextSong.context_uri}")
                    } else {
                        Log.d("SongControl", "next Song is null")
                    }
                    runBlocking { roomManager.advanceSong(roomCode) }
                    if (nextSong != null) {
                        playSong(
                            nextSong.context_uri,
                            0,
                            uTokens
                        )
                    }
                }
            }
        ) {
            Image(painter = painterResource(id = R.drawable.next_track), contentDescription = null)
        }
    }
}
