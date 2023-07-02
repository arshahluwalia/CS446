package com.example.jukebox.songqueue

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.PlainTooltipState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.jukebox.AddSongActivity
import com.example.jukebox.R
import com.example.jukebox.SecondaryBackground
import com.example.jukebox.SettingsActivity
import com.example.jukebox.Song
import com.example.jukebox.ui.theme.DarkPurple
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.ui.theme.PurpleNeon
import com.example.jukebox.util.CopyToClipboard
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SongQueueScreenContent(
	hostName: String,
	isHost: Boolean,
	playingSong: Song,
	queuedSongList: List<Song>,
	roomCode: String = "",
	removeSong: (Song) -> Unit = { }
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
			SettingsButton(
				isHost = isHost,
				roomCode = roomCode
			)
			SongQueueTitle(hostName = hostName)
			RoomCode(roomCode = roomCode)
			SongQueue(
				isHost = isHost,
				playingSong = playingSong,
				queuedSongList = queuedSongList,
				removeSong = removeSong,
				roomCode = roomCode
			)
		}
	}
}
@Composable
fun SettingsButton(
	isHost: Boolean,
	roomCode: String = "",
) {
	if (isHost) {
		val context = LocalContext.current
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 10.dp),
			horizontalArrangement = Arrangement.End,
			verticalAlignment = Alignment.CenterVertically,
		) {
			IconButton(
				onClick = {
					val intent = Intent(context, SettingsActivity::class.java)
					intent.putExtra("roomCode", roomCode)
					context.startActivity(intent)
				}) {
				Image(
					modifier = Modifier
						.size(30.dp),
					painter = painterResource(id = R.drawable.settings_button),
					contentDescription = null
				)
			}
		}
	}
	else {
		Row(
			modifier = Modifier.padding(top = 35.dp, start = 20.dp, end = 20.dp, bottom = 35.dp)
		) {}
	}
}

@Composable
fun SongQueueTitle(
	hostName: String,
) {
	Text(
		modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomCode(
	roomCode: String,
) {
	val context = LocalContext.current
	val tooltipState = remember { PlainTooltipState() }
	val scope = rememberCoroutineScope()

	Row(
		modifier = Modifier.padding(bottom = 30.dp),
		verticalAlignment = Alignment.CenterVertically)
	{
		Text(
			text = buildAnnotatedString {
				append("The room code is ")
				withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
					append(roomCode)
				}
			},
			color = Color.White,
			style = MaterialTheme.typography.headlineSmall,
			textAlign = TextAlign.Center,
		)
		PlainTooltipBox(
			tooltip = { Text("Copied to clipboard") },
			tooltipState = tooltipState
		) {
			Image(
				modifier = Modifier
					.size(20.dp)
					.clickable {
						CopyToClipboard.copyToClipboard(context, "roomCode", roomCode)
						scope.launch { tooltipState.show() }
					}
					.tooltipAnchor(),
				painter = painterResource(id = R.drawable.copy_to_clipboard),
				contentDescription = null,
			)
		}
	}
}

@Composable
fun SongQueue(
	isHost: Boolean,
	playingSong: Song,
	queuedSongList: List<Song>,
	removeSong: (Song) -> Unit = {},
	roomCode: String
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(start = 40.dp, end = 30.dp),
		horizontalAlignment = Alignment.End
	) {
		PlayingSong(playingSong = playingSong, isHost = isHost, roomCode= roomCode)
		QueuedSongs(queuedSongList = queuedSongList, isHost = isHost, removeSong = removeSong)
	}
}

@Composable
fun PlayingSong(
	playingSong: Song,
	isHost: Boolean,
	roomCode: String
) {
	Column(
		modifier = Modifier
			.clip(shape = RoundedCornerShape(10.dp))
			.background(color = PurpleNeon),
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)) {
				Text(text = playingSong.songTitle, color = Color.White)
				Text(text = playingSong.songArtist, color = Color.White)
			}
			SongProgressBar()
		}
		if (isHost) {
			SongControl(roomCode)
		}
	}
}

@Composable
fun SongProgressBar(){
	Column(modifier = Modifier
		.fillMaxWidth()
		.padding(end = 20.dp, top = 10.dp)
	) {
		LinearProgressIndicator(
			modifier = Modifier
				.fillMaxWidth(),
			trackColor = Color.LightGray,
			color = DarkPurple,
			progress = 0.3f
		)
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(text = "0:00", style = MaterialTheme.typography.bodySmall)
			Text(text = "3:00", style = MaterialTheme.typography.bodySmall)
		}
	}
}

@Composable
fun QueuedSongs(
	queuedSongList: List<Song>,
	isHost: Boolean,
	removeSong: (Song) -> Unit = { }
) {
	if (isHost) {
		queuedSongList.forEach { song ->
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				var isSongUpvoted by remember{
					mutableStateOf(false)
				}
				HostSongItem(song = song, removeSong = removeSong)
//				UpvoteButton(song = song, isUpvoted = isSongUpvoted, onVoteClick = {
//					isSongUpvoted = !isSongUpvoted
//				})
			}
		}
	}
	queuedSongList.forEach { song ->
		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Start
		) {
			var isSongUpvoted by remember{
				mutableStateOf(false)
			}
			GuestSongItem(song = song)
			UpvoteButton(song = song, isUpvoted = isSongUpvoted, onVoteClick = {
				isSongUpvoted = !isSongUpvoted
			})
		}
	}
}

@Composable
fun GuestSongItem(
	song: Song,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.fillMaxWidth(fraction = 0.85f)
	) {
		if (song.isApproved) {
			Image(
				modifier = Modifier
					.size(30.dp)
					.clickable { /* TODO: Add tooltip explaining that host needs to approve*/ },
				painter = painterResource(id = R.drawable.approve_check),
				contentDescription = null
			)
		} else {
			Column(modifier = Modifier.padding(start = 30.dp)) {}
		}
		Column(modifier = Modifier
			.padding(top = 10.dp, bottom = 10.dp)
			.clickable { /* TODO: Redirects to spotify */ },
		) {
			Text(text = song.songTitle, color = Color.White)
			Text(text = song.songArtist, color = Color.LightGray)
			Text(text = "upvotes: " + song.votes, color = Color.LightGray)
		}
	}
}

@Composable
fun UpvoteButton(song: Song, isUpvoted: Boolean, onVoteClick: () -> Unit){
	Image(
		modifier = Modifier
			.clickable { /*If the user hasn't upvoted, increment upvotes by one*/
				if(!isUpvoted){
					song.upvote()
				}
				else{ /*If user has upvoted: undo the upvote*/
					song.downvote()
				}
				onVoteClick()
			}
			.alpha(if(isUpvoted) 0.5f else 1.0f),
		painter = painterResource(id = R.drawable.upvote_arrow),
		contentDescription = null
	)
}

@Composable
fun HostSongItem(
	song: Song,
	removeSong: (Song) -> Unit = { }
) {
	val expanded = remember { mutableStateOf(false) }
	Column(modifier = Modifier
		.padding(15.dp)
		.clickable { /* TODO: Redirects to spotify */ },
	) {
		Text(text = song.songTitle, color = Color.White)
		Text(text = song.songArtist, color = Color.White)
	}
	Row(verticalAlignment = Alignment.CenterVertically) {
		Image(
			modifier = Modifier
				.size(30.dp)
				.clickable { /* TODO: approve */ }
				.padding(start = 10.dp),
			painter = if (song.isApproved) {
				painterResource(id = R.drawable.approve_check_purple)
			} else {
				painterResource(id = R.drawable.approve_check)
			},
			contentDescription = null
		)
		Image(
			modifier = Modifier
				.size(30.dp)
				.clickable { /* TODO: deny */ }
				.padding(start = 10.dp),
			painter = if (song.isApproved) {
				painterResource(id = R.drawable.deny_x)
			} else {
				painterResource(id = R.drawable.deny_x_purple)
			},
			contentDescription = null
		)
		Box(modifier = Modifier.padding(start = 10.dp)) {
			Image(
				modifier = Modifier
					.size(20.dp)
					.clickable { expanded.value = true },
				painter = painterResource(id = R.drawable.ellipsis),
				contentDescription = null
			)
			DropdownMenu(
				expanded = expanded.value,
				onDismissRequest = { expanded.value = !expanded.value }
			) {
				DropdownMenuItem(
					text = { Text(text = "Remove Song") },
					onClick = { removeSong(song) }
				)
			}
		}
	}
}

@Preview
@Composable
private fun PreviewScreenContent() {
	JukeboxTheme() {
		SecondaryBackground()
		Column(
			modifier = Modifier.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			SongQueueScreenContent(
				hostName = "Lucas",
				isHost = false,
				playingSong = Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = true),
				queuedSongList = listOf(
					Song(songTitle = "What makes you beautifullllllllllllll", songArtist = "Shakira", isApproved = true),
					Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
					Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
					Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
					Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
					Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
					Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
					Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
					Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", isApproved = false),
				),
				roomCode = "ABCDE"
			)
		}
	}
}