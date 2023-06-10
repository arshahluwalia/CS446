package com.example.jukebox

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.ui.theme.PurpleNeon

class SongQueueActivity  : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JukeboxTheme() {
                ScreenContent(
                    hostName = "Lucas",
                    isHost = false,
                    playingSong = Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = true),
                    queuedSongList = listOf(
                        Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = true),
                        Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                        Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                        Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                        Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                        Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                        Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                        Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                        Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    )
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenContent(
    hostName: String,
    isHost: Boolean,
    playingSong: Song,
    queuedSongList: List<Song>
) {
    val context = LocalContext.current
    // TODO: handle song names that are too long (cut off and auto scroll horizontally)
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddSongActivity::class.java)
                    context.startActivity(intent)
                },
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }
    ) {
        Column(modifier = Modifier.reusableBackground()) { }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SongQueueTitle(hostName = hostName)
            SongQueue(
                isHost = isHost,
                playingSong = playingSong,
                queuedSongList = queuedSongList
            )
        }
    }
}

@Composable
private fun SongQueueTitle(
    hostName: String,
) {
    // TODO: get host name, change "tonight" wording to reflect time of day or just use "today"
    Text(
        modifier = Modifier.padding(top = 70.dp, start = 20.dp, end = 20.dp, bottom = 30.dp),
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(hostName)
            }
            append(" is on aux tonight")
        },
        color = Color.White,
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun SongQueue(
    isHost: Boolean,
    playingSong: Song,
    queuedSongList: List<Song>
) {
    Column(
        modifier = Modifier.padding(start = 50.dp, end = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayingSong(playingSong = playingSong)
        if (isHost) {
            QueuedSongs(queuedSongList = queuedSongList)
        } else {
            QueuedSongs(queuedSongList = queuedSongList)
        }
    }
}

@Composable
fun PlayingSong(
    playingSong: Song
) {
    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .background(color = PurpleNeon),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)) {
            Text(text = playingSong.songTitle, color = Color.White)
            Text(text = playingSong.songArtist, color = Color.White)
        }
        Image(
            modifier = Modifier
                .padding(end = 20.dp, top = 10.dp, bottom = 10.dp)
                .clickable { /* TODO: Redirects to spotify */ },
            painter = painterResource(id = R.drawable.currently_playing), contentDescription = null
        )
    }
}

@Composable
fun QueuedSongs(
    queuedSongList: List<Song>
) {
    queuedSongList.forEach { song ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SongItem(song = song)
        }
    }
}

@Composable
fun SongItem(song: Song) {
    Row(
        modifier = Modifier.padding(start = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (song.isApproved) {
            Image(
                modifier = Modifier.size(30.dp),
                painter = painterResource(id = R.drawable.approved_check),
                contentDescription = null
            )
        } else {
            Column(modifier = Modifier.padding(start = 30.dp)) {}
        }
        Column(modifier = Modifier
            .padding(20.dp)
            .clickable { /* TODO: Redirects to spotify */ },
        ) {
            Text(text = song.songTitle, color = Color.White)
            Text(text = song.songArtist, color = Color.White)
        }
    }
    Image(
        modifier = Modifier
            .padding(end = 50.dp)
            .clickable { /* TODO: upvote song */ },
        painter = painterResource(id = R.drawable.upvote_arrow),
        contentDescription = null
    )
}

@Preview
@Composable
private fun PreviewScreenContent() {
    JukeboxTheme() {
        Column(modifier = Modifier.reusableBackground()) { }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenContent(
                hostName = "Lucas",
                isHost = false,
                playingSong = Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = true),
                queuedSongList = listOf(
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = true),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
                )
            )
        }
    }
}