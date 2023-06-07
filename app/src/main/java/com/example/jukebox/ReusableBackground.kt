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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.ui.theme.Black
import com.example.jukebox.ui.theme.PurpleNeon

class ReusableBackground : ComponentActivity() {

}
fun Modifier.reusableBackground() = composed {
    val configuration = LocalConfiguration.current
//        val screenHeight = configuration.screenHeightDp.dp
//        val screenWidth = configuration.screenWidthDp.dp
    this.fillMaxSize()
        .scale(0.0F, 2.0F)
        .background(
            brush = Brush.radialGradient(
                colors = listOf(
                    PurpleNeon,
                    Black
                ),
            )
        )
}


