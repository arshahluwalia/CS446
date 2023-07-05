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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.jukebox.R
import com.example.jukebox.spotify.task.SpotifySongControlTask.getPlaybackState
import com.example.jukebox.spotify.task.SpotifySongControlTask.pauseSong
import com.example.jukebox.spotify.task.SpotifySongControlTask.playPreviousSong
import com.example.jukebox.spotify.task.SpotifySongControlTask.playSong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun SongControl(
    hostToken: MutableStateFlow<String>,
    userTokens: MutableStateFlow<MutableList<String>>
) {
    val scope = rememberCoroutineScope()
    val hToken = hostToken.collectAsState().value
    val uTokens = userTokens.collectAsState().value

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
                        playPreviousSong(uTokens)
                        playSong(
                            "spotify:album:5ht7ItJgpBH7W6vJ5BqpPr",
                            0,
                            uTokens)
                    }
            }
        ) {
            Image(painter = painterResource(id = R.drawable.previous_track), contentDescription = null)
        }
        Button(
            onClick = {
                scope.launch {
                    pauseSong(uTokens)
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
                    playPreviousSong(uTokens)
                    val playBackState = getPlaybackState(hToken)
                    playBackState.first?.let { Log.d("spotify fetch state: context_uri", it) }
                    Log.d("spotify fetch state: offset", playBackState.second.toString())
                }
            }
        ) {
            Image(painter = painterResource(id = R.drawable.next_track), contentDescription = null)
        }
    }
}
