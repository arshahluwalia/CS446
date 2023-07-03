package com.example.jukebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.spotify.task.SpotifySearchTask
import com.example.jukebox.ui.theme.JukeboxTheme
import kotlinx.coroutines.flow.MutableStateFlow

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomCode = intent.getStringExtra("roomCode").toString()
        val dispatcher = onBackPressedDispatcher
        setContent {
            JukeboxTheme() {
                ScreenContent(
                    dispatcher = dispatcher,
                    roomCode = roomCode,
                )
            }
        }
    }
}

@Composable
private fun ScreenContent(
    dispatcher: OnBackPressedDispatcher? = null,
    roomCode: String,
) {
    Box {
        SecondaryBackground()
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                BackToQueueButton(dispatcher)
            }
            SettingsTitle()
        }
    }
}

@Composable
private fun BackToQueueButton(dispatcher: OnBackPressedDispatcher? = null) {
    TextButton(
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        shape = RoundedCornerShape(20),
        onClick = {
            dispatcher?.onBackPressed()
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier.padding(end = 10.dp),
                painter = painterResource(
                    id = R.drawable.arrow_back
                ),
                contentDescription = null
            )
            Text(
                text = "Back to Queue",
                color = Color.White,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
private fun SettingsTitle() {
    Text(
        text = "Session Settings",
        style = MaterialTheme.typography.titleSmall,
        color = Color.White,
        textAlign = TextAlign.Center
    )
}

@Composable
@Preview
private fun PreviewScreenContent() {
    JukeboxTheme() {
        ScreenContent(
            dispatcher = null,
            roomCode = "ABCDE",
        )
    }
}
