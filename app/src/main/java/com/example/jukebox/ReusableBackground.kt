package com.example.jukebox

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.jukebox.ui.theme.OffBlack
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
                    OffBlack
                ),
            )
        )
}

@Composable
fun PrimaryBackground() {
    Image(
        modifier = Modifier.scale(1.2f).background(color = Color.Black).fillMaxSize(),
        painter = painterResource(id = R.drawable.primary_background),
        contentDescription = null
    )
}

@Composable
fun SecondaryBackground() {
    Image(
        modifier = Modifier.scale(2.0f).background(color = Color.Black).fillMaxSize(),
        painter = painterResource(id = R.drawable.secondary_background),
        contentDescription = null
    )
}


