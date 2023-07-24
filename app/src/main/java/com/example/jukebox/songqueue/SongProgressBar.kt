package com.example.jukebox.songqueue

import android.annotation.SuppressLint
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jukebox.CurrentSong
import com.example.jukebox.Song
import com.example.jukebox.spotify.task.SpotifySongControlTask.playSong
import com.example.jukebox.util.SongTimer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun SongProgressBar(
    isHost: Boolean,
    userTokens: MutableStateFlow<MutableList<String>>,
    currentSong: Song
) {
    val uTokens = userTokens.collectAsState().value
    val duration = currentSong.duration
    var sliderValue by remember { mutableStateOf(0f) }
    var userSliderValue: MutableStateFlow<Float> = MutableStateFlow(0f)
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        sliderValue = duration - CurrentSong.currentTime.collectAsState().value.toFloat() * 1000

        Slider(
            modifier = Modifier
                .fillMaxWidth(),
            value = sliderValue,
            onValueChange = { newValue ->
                userSliderValue.value = newValue
            },
            onValueChangeFinished = {
                scope.launch {
                    if (currentSong.songTitle != "" && isHost) {
                        Log.d("play seek: ", formatDuration(userSliderValue.value.toInt()))
                        playSong(currentSong.context_uri, userSliderValue.value.toInt(), uTokens)
                        CurrentSong.setDuration(duration - userSliderValue.value.toInt(), sliderValue.toInt())
                        Log.d("play seek: slider value", formatDuration(sliderValue.toInt()))
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
            Text(text = formatDuration(sliderValue.toInt()), style = MaterialTheme.typography.bodySmall, color = Color.White)
            if (duration != 0){
                Text(text = formatDuration(duration), style = MaterialTheme.typography.bodySmall, color = Color.White)
            } else {
                Text(text = "0:00", style = MaterialTheme.typography.bodySmall, color = Color.White)
            }
        }
    }
}
fun formatDuration(durationMillis: Int): String {
    val seconds = (durationMillis / 1000) % 60
    val minutes = (durationMillis / 1000) / 60
    return "%d:%02d".format(minutes, seconds)
}
