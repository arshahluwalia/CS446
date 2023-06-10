package com.example.jukebox

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jukebox.ui.theme.Black

class AddSongActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                ScreenContent()
            }
        }
    }

    @Composable
    fun ScreenContent() {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenBackground()
            BackToQueueButton()
            HostTitle()
            AddSongBox()
        }
    }

    @Composable
    fun BoxWithConstraintsScope.ScreenBackground() {
        // TODO: need to update to reusable background
        val aspectRatio = maxWidth / maxHeight
        Box(
            Modifier
                .fillMaxSize()
                .scale(maxOf(aspectRatio, 1f), maxOf(1 / aspectRatio, 1f))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Red,
                            Black
                        )
                    )
                )
        )
    }

    @Composable
    fun BoxWithConstraintsScope.BackToQueueButton() {
        // TODO: need to make look less like a button, maybe clickable text
        val context = LocalContext.current
        Button(
            modifier = Modifier
                .align(Alignment.TopStart),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp,
                pressedElevation = 15.dp,
                disabledElevation = 0.dp
            ),
            shape = RoundedCornerShape(20),
            onClick = {
                val intent = Intent(context, SongQueueActivity::class.java)
                context.startActivity(intent)
            }
        ) {
            Text(text = AnnotatedString("Back to Queue"))
        }
    }

    @Composable
    fun BoxWithConstraintsScope.HostTitle() {
        var host = "Lucas" // TODO: probably going to set host globally when room is created, this is a filler for now
        Column(
            modifier = Modifier
                .padding(top = maxHeight / 4)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "$host is on aux tonight", // TODO: maybe underline host name like in Figma
                fontSize = 40.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun BoxWithConstraintsScope.AddSongBox() {
        Column(
            modifier = Modifier
                .padding(top =  maxHeight*9 / 20)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // TODO: replace filler text with container
            Text(text = "Add Song Container", color = Color.Black)
        }
    }
}
