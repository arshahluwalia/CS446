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
import com.example.jukebox.spotify.task.SpotifySongControlTask.playSong
import com.example.jukebox.spotify.task.SpotifySongControlTask.resumeSong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private fun getHostToken(
    roomCode: String,
    roomManager: RoomManager?
): String? {
    val token = runBlocking { roomManager?.getHostToken(roomCode) }
    if (token != null) {
        return token
    }
    return null
}

private fun getUserTokens(
    roomCode: String,
    roomManager: RoomManager?
): MutableList<String> {
    val users = runBlocking { roomManager?.getUsers(roomCode) }
    val tokens = mutableListOf<String>()
    if (users != null) {
        users.forEach { tokens.add(it.userToken) }
    }
    return tokens
}

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun SongControl(roomCode: String, roomManager: RoomManager?) {
    val scope = rememberCoroutineScope()
    var hostToken = getHostToken(roomCode, roomManager)
    var userTokens = getUserTokens(roomCode, roomManager)
    if (hostToken != null) {
        userTokens.add(hostToken)
    }
    var isPaused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                    Log.d("spotify control: token list", userTokens.toString())
                if (hostToken != null) {
                    Log.d("spotify control: host token", hostToken)
                }
                    scope.launch {
                       // playPreviousSong(userTokenList)
                        playSong("spotify:album:5ht7ItJgpBH7W6vJ5BqpPr", 0, userTokens)
                    }
            }
        ) {
            Image(painter = painterResource(id = R.drawable.previous_track), contentDescription = null)
        }
        Button(
            onClick = {
                scope.launch {
                    if (isPaused) {
                        resumeSong(userTokens)
                        isPaused = false
                    } else {
                        pauseSong(userTokens)
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
                if (hostToken != null) {
                    Log.d("spotify fetch state: host token", hostToken)
                }
                scope.launch {
                   // playPreviousSong(userTokenList)
//                    val playBackState = hostToken?.let { getPlaybackState(it) }
//                    if (playBackState != null) {
//                        playBackState.first?.let { Log.d("spotify fetch state: context_uri", it) }
//                        Log.d("spotify fetch state: offset", playBackState.second.toString())
//                    }
                }
            }
        ) {
            Image(painter = painterResource(id = R.drawable.next_track), contentDescription = null)
        }
    }
}

// Test this function before use
fun syncSong(roomCode: String, userToken: String) {
    val roomManager = RoomManager()
    var hostToken = getHostToken(roomCode, roomManager)

    val playBackState = runBlocking { hostToken?.let { getPlaybackState(it) } }
    val context_uri = playBackState?.first
    val offset = playBackState?.second

    runBlocking {
        if (context_uri != null && offset != null) {
            playSong(context_uri, offset, mutableListOf(userToken))
        }
    }
}
