package com.example.jukebox

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.spotify.SpotifyUserToken
import com.example.jukebox.spotify.task.SpotifySearchTask.requestTrackID
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.util.HideSoftKeyboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddSongActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomCode = intent.getStringExtra("roomCode").toString()
        val isHost = intent.getBooleanExtra("isHost", false)
        val dispatcher = onBackPressedDispatcher
        val roomManager = RoomManager()
        val maxSongRequests = MutableStateFlow(0)
        getMaxSongRequests(roomCode, roomManager, maxSongRequests)

        val songQueue = MutableStateFlow<List<Song>>(emptyList())
        val approvedSongQueue = MutableStateFlow<List<Song>>(emptyList())
        val deniedSongQueue = MutableStateFlow<List<Song>>(emptyList())
        getSongQueueByOrderAdded(roomCode, songQueue)
        getApprovedSongQueue(roomCode, approvedSongQueue)
        getDeniedSongQueue(roomCode, deniedSongQueue)

        setContent {
            val songName = MutableStateFlow("")
            val songList = MutableStateFlow<List<Song>>(emptyList())

            val concatSongQueue =
                approvedSongQueue.collectAsState().value + songQueue.collectAsState().value + deniedSongQueue.collectAsState().value

            JukeboxTheme {
                ScreenContent(
                    dispatcher = dispatcher,
                    roomCode = roomCode,
                    addToQueue = { addToQueue(songName.value, songList) },
                    songName = songName,
                    songList = songList,
                    queuedSongList = concatSongQueue,
                    activity = this,
                    roomManager = roomManager,
                    isHost = isHost,
                    remainingRequests = maxSongRequests.collectAsState().value
                )
            }
        }
    }

    private fun getSongQueueByOrderAdded(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        val roomManager = RoomManager()
        // update the songqueue, ordered by the timestamp by which it was added
        roomManager.getPendingQueue(roomCode) { queue ->
            songQueue.value = queue.queue.sortedBy { it.timeStampAdded }
        }
    }

    private fun getApprovedSongQueue(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        val roomManager = RoomManager()
        roomManager.getApprovedQueueCallback(roomCode) { queue ->
            songQueue.value = queue.queue
        }
    }

    private fun getDeniedSongQueue(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        val roomManager = RoomManager()
        roomManager.getDeniedQueueCallback(roomCode) { queue ->
            songQueue.value = queue.queue
        }
    }

    private suspend fun addToQueue(songName: String, mutableSongList: MutableStateFlow<List<Song>>) {
        val songList = requestTrackID(songName)
        mutableSongList.value = songList
    }

    private fun getMaxSongRequests(
        roomCode: String,
        roomManager: RoomManager?,
        maxSongRequests: MutableStateFlow<Int>
    ) {
        roomManager?.getMaxSuggestions(roomCode) { maxRequests ->
            roomManager.getCurrentSuggestions(roomCode, SpotifyUserToken.getToken()) { currentRequests ->
                if (maxRequests - currentRequests < 0) {
                    maxSongRequests.value = 0
                } else {
                    maxSongRequests.value = maxRequests - currentRequests
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    dispatcher: OnBackPressedDispatcher? = null,
    roomCode: String,
    addToQueue: suspend () -> Unit,
    songName: MutableStateFlow<String>,
    songList: MutableStateFlow<List<Song>>,
    queuedSongList: List<Song>,
    activity: Activity?,
    roomManager: RoomManager?,
    isHost: Boolean,
    remainingRequests: Int
) {
    Box {
        SecondaryBackground()
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                BackToQueueButton(dispatcher)
            }
            AddSongTitle()
            if (!isHost) {
                SongRequestsRemaining(remainingRequests)
            }
            AddSongBox(
                roomCode = roomCode,
                addToQueue = addToQueue,
                songName = songName,
                songList = songList,
                queuedSongList = queuedSongList,
                activity = activity,
                roomManager = roomManager,
                isHost = isHost,
                remainingRequests = remainingRequests
            )
        }
    }
}

@Composable
private fun BackToQueueButton(dispatcher: OnBackPressedDispatcher? = null) {
    TextButton(
        onClick = { dispatcher?.onBackPressed() }
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
private fun AddSongTitle() {
    Text(
        text = "What do you want to listen to?",
        style = MaterialTheme.typography.titleSmall,
        color = Color.White,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun SongRequestsRemaining(remainingRequests: Int) {
    Text(
        modifier = Modifier.padding(top = 20.dp),
        text = buildAnnotatedString {
            append("You have ")
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(remainingRequests.toString())
            }
            append(" song requests remaining")
        },
        color = Color.White,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun AddSongBox(
    roomCode: String,
    addToQueue: suspend () -> Unit,
    songName: MutableStateFlow<String>,
    songList: MutableStateFlow<List<Song>>,
    queuedSongList: List<Song>,
    activity: Activity?,
    roomManager: RoomManager?,
    isHost: Boolean,
    remainingRequests: Int
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
            Row(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBox(
                    addToQueue = addToQueue,
                    songName = songName,
                    activity = activity
                )
                IconButton(
                    onClick = {
                        if (activity != null) {
                            HideSoftKeyboard.hideSoftKeyboard(activity)
                        }
                        scope.launch { addToQueue() }
                    }
                ) {
                    Image(painter = painterResource(id = R.drawable.arrow_forward), contentDescription = null)
                }
            }
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp)) {
                SearchSongQueue(
                    queuedSongList = queuedSongList,
                    songList = songList.collectAsState().value,
                    roomCode = roomCode,
                    roomManager = roomManager,
                    isHost = isHost,
                    remainingRequests = remainingRequests
                )
            }
        }
    }
}

@Composable
private fun SearchBox(
    addToQueue: suspend () -> Unit,
    songName: MutableStateFlow<String>,
    activity: Activity?
) {
    val scope = rememberCoroutineScope()

    TextField(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
        value= songName.collectAsState().value,
        shape = RoundedCornerShape(20),
        singleLine = true,
        label = { Text(text = "Enter song name") },
        onValueChange = { songName.value = it },
        keyboardActions = KeyboardActions(
            onDone = {
                // search, parse, populate, choose add
                if (activity != null) {
                    HideSoftKeyboard.hideSoftKeyboard(activity)
                }
                scope.launch { addToQueue() }
            }
        ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
private fun SearchSongQueue(
    queuedSongList: List<Song>,
    songList: List<Song>,
    roomCode: String,
    roomManager: RoomManager?,
    isHost: Boolean,
    remainingRequests: Int
) {
    val context = LocalContext.current
    val clickedStateMap = remember { mutableStateMapOf<String, Boolean>() }

    Log.d("Display: ", "Songs to add: $songList")
    songList.forEach { song ->
        queuedSongList.forEach{ if (it.context_uri == song.context_uri) clickedStateMap[song.context_uri] = true }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            SearchSongItem(song = song)
            IconButton(
                onClick = {
                    if (remainingRequests > 0) {
                        roomManager?.addSongToPendingQueue(roomCode, song)
                        clickedStateMap[song.context_uri] = true
                        if (!isHost) roomManager?.suggestSong(roomCode, SpotifyUserToken.getToken())
                    } else {
                        AlertDialog.Builder(context)
                            .setTitle("You have exceeded the max amount of song requests")
                            .setMessage("Please try again later")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
            ) {
                Icon(
                    imageVector = if (clickedStateMap[song.context_uri] == true) Icons.Filled.Check else Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}


@Composable
private fun SearchSongItem(song: Song) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(start = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier
            .padding(15.dp)
        ) {
            Text(text = song.songTitle, color = Color.White)
            Text(text = song.songArtist, color = Color.White)
        }
    }
}

//@Composable
//@Preview
//private fun PreviewScreenContent() {
//    JukeboxTheme {
//        ScreenContent(
//            dispatcher = null,
//            roomCode = "ABCDE",
//            addToQueue = {  },
//            songName = MutableStateFlow("Hello"),
//            songList = MutableStateFlow(listOf(
//                Song(songArtist = "Adele", songTitle = "Hello"),
//                Song(songArtist = "Adele", songTitle = "Hello"),
//                Song(songArtist = "Adele", songTitle = "Hello"),
//            )),
//            activity = null,
//            roomManager = null,
//            isHost = false,
//            remainingRequests = 5
//        )
//    }
//}
