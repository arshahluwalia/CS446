package com.example.jukebox

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.spotify.task.SpotifySearchTask.requestTrackID
import com.example.jukebox.ui.theme.JukeboxTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddSongActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomCode = intent.getStringExtra("roomCode").toString()
        val dispatcher = onBackPressedDispatcher
        setContent {
            val songName = MutableStateFlow("")
            val songList = MutableStateFlow<List<Song>>(emptyList())
            JukeboxTheme() {
                ScreenContent(
                    dispatcher = dispatcher,
                    roomCode = roomCode,
                    addToQueue = { addToQueue(songName.value, songList) },
                    songName = songName,
                    songList = songList
                )
            }
        }
    }

    private suspend fun addToQueue(songName: String, mutableSongList: MutableStateFlow<List<Song>>) {
        val songList = requestTrackID(songName)
        mutableSongList.value = songList
    }
}

@Composable
private fun ScreenContent(
    dispatcher: OnBackPressedDispatcher? = null,
    roomCode: String,
    addToQueue: suspend () -> Unit,
    songName: MutableStateFlow<String>,
    songList: MutableStateFlow<List<Song>>,
) {
    Box {
        SecondaryBackground()
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                BackToQueueButton(dispatcher)
            }
            AddSongTitle()
            AddSongBox(roomCode, addToQueue, songName, songList)
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
private fun AddSongBox(
    roomCode: String,
    addToQueue: suspend () -> Unit,
    songName: MutableStateFlow<String>,
    songList: MutableStateFlow<List<Song>>
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp, start = 20.dp, end = 20.dp, bottom = 70.dp)
                .clip(shape = RoundedCornerShape(20.dp))
                .background(color = Color.Black.copy(alpha = 0.4f))
        ){
            TextField(
                modifier = Modifier
                    .padding(top = 30.dp, start = 20.dp, end = 20.dp),
                value= songName.collectAsState().value,
                shape = RoundedCornerShape(20),
                singleLine = true,
                label = {
                    Text(
                        text = "Enter song name",
                    )
                },
                onValueChange = {
                    songName.value = it
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        // search, parse, populate, choose add
                        // TODO: only add song when they are chosen, here we should display
                        scope.launch {
                            addToQueue()
                        }
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                )
            )
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp)) {
                SearchSongQueue(queuedSongList = songList.collectAsState().value, roomCode)
            }
        }
    }
}


@Composable
private fun SearchSongQueue(
    queuedSongList: List<Song>,
    roomCode: String
) {
    Log.d("Display: ", "Songs to add: $queuedSongList")
    queuedSongList.forEach { song ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SearchSongItem(song = song, roomCode)
        }
    }
}


@Composable
private fun SearchSongItem(song: Song, roomCode: String) {
    val roomManager = RoomManager()
    var isClicked by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.padding(start = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier
            .padding(15.dp)
        ) {
            Text(text = song.songTitle, color = Color.White)
            Text(text = song.songArtist, color = Color.White)
        }
    }
    // TODO: Reset the button to add when a new search is made
    IconButton(
        onClick = {
        roomManager.addSongToQueue(roomCode, song)
        isClicked = true
    }) {
        Icon(
            imageVector = if (isClicked) Icons.Filled.Check else Icons.Filled.Add,
            contentDescription = null,
            tint = Color.White)
    }
}

@Composable
@Preview
private fun PreviewScreenContent() {
    JukeboxTheme() {
        ScreenContent(
            dispatcher = null,
            roomCode = "ABCDE",
            addToQueue = {  },
            songName = MutableStateFlow("Hello"),
            songList = MutableStateFlow(listOf(
                Song(songArtist = "Adele", songTitle = "Hello"),
                Song(songArtist = "Adele", songTitle = "Hello"),
                Song(songArtist = "Adele", songTitle = "Hello"),
            ))
        )
    }
}
