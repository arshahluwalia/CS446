package com.example.jukebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.ui.theme.Black
import com.example.jukebox.ui.theme.PurpleNeon

fun Modifier.reusableBackground() = composed {
    val configuration = LocalConfiguration.current
    val boxHeight = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }
    val boxWidth = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val aspectRatio = boxWidth/boxHeight
    this.fillMaxSize()
        .scale(maxOf(aspectRatio, 1f), maxOf(3 / aspectRatio, 1f))
        .background(
            brush = Brush.radialGradient(
                center = Offset(x = 0F, y = 2*boxHeight / 3),
                colors = listOf(
                    PurpleNeon,
                    Black
                ),
            )
        )
}


