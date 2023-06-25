package com.example.jukebox

import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.jukebox.spotify.SpotifyApiTask.requestTrackID

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
            AddSongBox()
        }
    }

    @Composable
    fun BoxWithConstraintsScope.ScreenBackground() {
        Box(
            modifier = Modifier.reusableBackground()
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
                onBackPressedDispatcher.onBackPressed()
            }
        ) {
            Text(text = AnnotatedString("Back to Queue"))
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BoxWithConstraintsScope.AddSongBox() {
        var songName by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .padding(top = maxHeight * 3 / 20)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // TODO: round corner of container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp, start = 20.dp, end = 20.dp, bottom = 70.dp)
                    .background(Color.Black)

            ){
                TextField(
                    modifier = Modifier
                        .padding(top = 30.dp, start = 20.dp, end = 20.dp)
                        .align(Alignment.TopCenter),
                    value= songName,
                    shape = RoundedCornerShape(20),
                    singleLine = true,
                    label = {
                        Text(
                            text = "Enter song name",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    onValueChange = {
                        songName = it
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // search, parse, populate, choose add
                            Log.d("textfield", songName)
                            requestTrackID(songName)
                        }
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    )
                )
            }
        }
    }
}
