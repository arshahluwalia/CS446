package com.example.jukebox.songqueue

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jukebox.Song
import com.example.jukebox.spotify.task.SpotifySongControlTask.playSong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun SongProgressBar(
    isHost: Boolean,
    userTokens: MutableStateFlow<MutableList<String>>,
    currentSong: Song
) {
    val uTokens = userTokens.collectAsState().value
    var duration = 50000
    var sliderValue by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Slider(
            modifier = Modifier
                .fillMaxWidth(),
            value = sliderValue,
            onValueChange = { newValue ->
                sliderValue = newValue
            },
            onValueChangeFinished = {
                scope.launch {
                    if (currentSong.songTitle != "" && isHost) {
                        Log.d("play seek: ", currentSong.context_uri)
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
