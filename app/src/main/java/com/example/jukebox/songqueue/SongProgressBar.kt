package com.example.jukebox.songqueue

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jukebox.RoomManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.jukebox.Song
import com.example.jukebox.spotify.task.SpotifySongControlTask.playSong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking


@Composable
fun SongProgressBar(
    isHost: Boolean,
    hostToken: MutableStateFlow<String>,
    userTokens: MutableStateFlow<MutableList<String>>,
    roomCode: String){
    val hToken = hostToken.collectAsState().value
    val uTokens = userTokens.collectAsState().value
    uTokens.add(hToken)
    var roomManager = RoomManager()
//    var currentSong = runBlocking{ roomManager.getCurrentSong(roomCode) }
    var currentSong = Song(
        context_uri = "spotify:track:5jzKL4BDMClWqRguW5qZvh",
        songArtist = "Katy Perry",
        duration = 227741
    )
    Log.d("playing song", currentSong?.duration.toString())
    var duration = currentSong?.duration
//    var duration = 100
    var sliderValue by remember { mutableStateOf(0f) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Slider(
            modifier = Modifier
                .fillMaxWidth(),
            value = sliderValue,
            onValueChange = { newValue ->
                if (isHost) {
                    sliderValue = newValue
                }
            },
            onValueChangeFinished = {
                runBlocking {
                    if (currentSong != null && isHost) {
                        playSong(currentSong.context_uri, sliderValue.toInt(), uTokens)
                    }
                }
            },
            valueRange = 0f..duration?.toFloat()!!,
            steps = 0,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "0:00", style = MaterialTheme.typography.bodySmall)
            if (duration != 0){
                Text(text = duration.toString(), style = MaterialTheme.typography.bodySmall)
            } else {
                Text(text = "0:00", style = MaterialTheme.typography.bodySmall)
            }

        }
    }
}
