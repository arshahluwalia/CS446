package com.example.jukebox

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.spotify.SpotifySearchTask.requestTrackID
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.SongQueue
import com.example.jukebox.RoomManager
import kotlinx.coroutines.launch

class AddSongActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomCode = intent.getStringExtra("roomCode").toString()
        val dispatcher = onBackPressedDispatcher
        setContent {
            ScreenContent(dispatcher, roomCode)
        }
    }
}

@Composable
private fun ScreenContent(dispatcher: OnBackPressedDispatcher? = null, roomCode: String) {
    JukeboxTheme() {
        Box {
            SecondaryBackground()
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    BackToQueueButton(dispatcher)
                }
                AddSongTitle()
                AddSongBox(roomCode)
            }
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
        Text(
            text = "Back to Queue",
            color = Color.White,
            textDecoration = TextDecoration.Underline
        )
    }
}

@Composable
private fun AddSongTitle() {
    Text(
        text = "What do you want to listen to?",
        style = MaterialTheme.typography.titleSmall,
        color = Color.White,
        textAlign = TextAlign.Center
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSongBox(roomCode: String) {
    val scope = rememberCoroutineScope()
    var songName by remember { mutableStateOf("") }
//    val database = Firebase.database.reference
    val roomManager = RoomManager()


    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp, start = 20.dp, end = 20.dp, bottom = 70.dp)
                .clip(shape = RoundedCornerShape(20.dp))
                .background(color = Color.Black.copy(alpha = 0.4f))
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
                    )
                },
                onValueChange = {
                    songName = it
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        // search, parse, populate, choose add
                        // TODO: only add song when they are chosen, here we should display
                        scope.launch {
                            var listOfSongs = requestTrackID(songName)
                            Log.d("Search: ", "Returned songs: $listOfSongs")
                        }
                        roomManager.addSongToQueue(roomCode,Song(songTitle = songName, context_uri = songName))
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQueueScreenContent(
    hostName: String,
    isHost: Boolean,
    playingSong: Song,
    queuedSongList: List<Song>,
    roomCode: String = ""
) {
    val context = LocalContext.current
    // TODO: handle song names that are too long (cut off and auto scroll horizontally)
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddSongActivity::class.java)
                    intent.putExtra("roomCode", roomCode)
                    context.startActivity(intent)
                },
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }
    ) {
        SecondaryBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SongQueue(
                isHost = isHost,
                playingSong = playingSong,
                queuedSongList = queuedSongList
            )
        }
    }
}

