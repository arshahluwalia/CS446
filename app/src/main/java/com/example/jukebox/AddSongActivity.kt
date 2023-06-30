package com.example.jukebox

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.spotify.SpotifySearchTask.requestTrackID
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.RoomManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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
    val roomManager = RoomManager()
    var listOfSongs = mutableListOf<Song>()

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
//                    .align(Alignment.TopCenter),
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
                        var job = CoroutineScope(Dispatchers.Main).async {
                            var songs = withContext(Dispatchers.IO) {
                                requestTrackID(songName)
                            }
                            Log.d("Add song: ", "Returned songs: $songs")
                            return@async songs
                        }
                        job.start()
                        Log.d("Add song: ", "Returned songs")
                        for (newSong in listOfSongs) {
                            Log.d("Add song: ", "in ADD activity: artist: ${newSong.songArtist}, title: ${newSong.songTitle}, uri: ${newSong.context_uri}")
                        }
//                        roomManager.addSongToQueue(roomCode,Song(songTitle = songName, context_uri = songName))
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                )
            )
            Column(
                modifier = Modifier.padding(start = 50.dp, end = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                searchSongQueue(queuedSongList = listOfSongs)
            }

        }
    }
}

@Composable
fun searchSongQueue(
    queuedSongList: List<Song>
) {
    Log.d("Display: ", "Songs to add: $queuedSongList")
    queuedSongList.forEach { song ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "helloworld", color = Color.White)
            searchSongItem(song = song)
        }
    }
}


@Composable
fun searchSongItem(song: Song) {
    // TODO: add isHost implementation, change icons if hose
    Row(
        modifier = Modifier.padding(start = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier
            .padding(15.dp)
        ) {
            Text(text = "helloworld", color = Color.White)
            Text(text = song.songTitle, color = Color.White)
            Text(text = song.songArtist, color = Color.White)
        }
    }
    Image(
        modifier = Modifier
            .padding(end = 50.dp)
            .clickable { /* TODO: add song */ },
        painter = painterResource(id = R.drawable.upvote_arrow),
        contentDescription = null
    )
}


