package com.example.jukebox.songqueue

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.PlainTooltipState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.AddSongActivity
import com.example.jukebox.ApprovalStatus
import com.example.jukebox.R
import com.example.jukebox.RoomManager
import com.example.jukebox.SecondaryBackground
import com.example.jukebox.SettingsActivity
import com.example.jukebox.Song
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.ui.theme.PurpleNeon
import com.example.jukebox.util.CopyToClipboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SongQueueScreenContent(
	dispatcher: OnBackPressedDispatcher? = null,
	hostName: String,
	isHost: Boolean,
	playingSong: Song,
	queuedSongList: List<Song>,
	roomCode: String = "",
	removeSong: (Song) -> Unit = { },
	roomManager: RoomManager?,
	appContext: Context,
	setApprovalStatus: (Song, ApprovalStatus) -> Unit = { _: Song, _: ApprovalStatus -> },
	maxSongUpvotes: Int,
	hostToken: MutableStateFlow<String> = MutableStateFlow(""),
	userTokens: MutableStateFlow<MutableList<String>> = MutableStateFlow(ArrayList()),
	mutableSongList: MutableStateFlow<List<Song>> = MutableStateFlow(ArrayList())
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
					intent.putExtra("isHost", isHost)
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
				.fillMaxSize(),
//				.verticalScroll(rememberScrollState()),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				BackButton(dispatcher)
				if (isHost) {
					SettingsButton(roomCode = roomCode)
				}
			}
			SongQueueTitle(hostName = hostName)
			RoomCode(roomCode = roomCode, appContext = appContext)
			if(!isHost){
				Text(text = "You have: $maxSongUpvotes upvotes remaining", color = Color.White)
			}
			SongQueue(
				isHost = isHost,
				playingSong = playingSong,
				queuedSongList = queuedSongList,
				removeSong = removeSong,
				roomCode = roomCode,
				roomManager = roomManager,
				setApprovalStatus = setApprovalStatus,
				maxSongUpvotes = maxSongUpvotes,
				hostToken = hostToken,
				userTokens = userTokens,
				mutableSongList = mutableSongList
			)
		}
	}
}
@Composable
private fun BackButton(dispatcher: OnBackPressedDispatcher? = null) {
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
				text = "Back",
				color = Color.White,
				textDecoration = TextDecoration.Underline
			)
		}
	}
}

@Composable
fun SettingsButton(
	roomCode: String = "",
) {
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
	appContext: Context
) {
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
						CopyToClipboard.copyToClipboard(appContext, "roomCode", roomCode)
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
	roomCode: String,
	roomManager: RoomManager?,
	setApprovalStatus: (Song, ApprovalStatus) -> Unit = { _: Song, _: ApprovalStatus -> },
	maxSongUpvotes: Int,
	hostToken: MutableStateFlow<String>,
	userTokens: MutableStateFlow<MutableList<String>>,
	mutableSongList: MutableStateFlow<List<Song>> = MutableStateFlow(ArrayList())
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(start = 40.dp, end = 30.dp),
		horizontalAlignment = Alignment.End
	) {
		PlayingSong(
			playingSong = playingSong,
			isHost = isHost,
			roomCode = roomCode,
			roomManager = roomManager,
			hostToken = hostToken,
			userTokens = userTokens
		)
		QueuedSongs(
			queuedSongList = queuedSongList,
			isHost = isHost,
			removeSong = removeSong,
			setApprovalStatus = setApprovalStatus,
			roomManager = roomManager,
			roomCode = roomCode,
			maxSongUpvotes = maxSongUpvotes,
			mutableSongList = mutableSongList
		)
	}
}

@Composable
fun PlayingSong(
	playingSong: Song,
	isHost: Boolean,
	roomCode: String,
	roomManager: RoomManager?,
	hostToken: MutableStateFlow<String>,
	userTokens: MutableStateFlow<MutableList<String>>
) {
	val expanded = remember { mutableStateOf(false) }
	Column(
		modifier = Modifier
			.clip(shape = RoundedCornerShape(10.dp))
			.background(color = PurpleNeon)
			.padding(bottom = 20.dp),
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Start
		) {
			Column(modifier = Modifier
				.fillMaxWidth(0.85f)
				.padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)) {
				Text(text = playingSong.songTitle, color = Color.White, overflow = TextOverflow.Ellipsis)
				Text(text = playingSong.songArtist, color = Color.White)
			}
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
						text = { Text(text = "Open in Spotify") },
						onClick = { /* TODO: open in spotify */ }
					)
				}
			}
		}
		SongProgressBar(
			isHost = isHost,
			userTokens = userTokens,
			roomCode = roomCode
		)
		if (isHost) {
			SongControl(
				userTokens = userTokens,
				roomCode = roomCode,
				roomManager = roomManager
			)
		}
	}
}

@Composable
fun QueuedSongs(
	queuedSongList: List<Song>,
	isHost: Boolean,
	removeSong: (Song) -> Unit = { },
	roomManager: RoomManager?,
	roomCode: String,
	setApprovalStatus: (Song, ApprovalStatus) -> Unit = { _: Song, _: ApprovalStatus -> },
	maxSongUpvotes: Int,
	mutableSongList: MutableStateFlow<List<Song>> = MutableStateFlow(ArrayList())
) {
	val data = MutableStateFlow(queuedSongList)
	if (isHost) {
//		val state = rememberReorderableLazyListState(onMove = { from, to ->
//			data.value = data.value.toMutableList().apply {
//				add(to.index, removeAt(from.index))
//			}
//		})
//
//		LazyColumn(
//			state = state.listState,
//			modifier = Modifier
//				.reorderable(state)
//				.detectReorderAfterLongPress(state)
//		) {
//			items(data.value, { it.context_uri }) {item ->
//				ReorderableItem(state, key = item.context_uri) { isDragging ->
//					val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
//					Column(
//						modifier = Modifier
//							.shadow(elevation.value)
//					) {
//						var isSongUpvoted by remember {
//							mutableStateOf(false)
//						}
//						Row(
//							modifier = Modifier.fillMaxWidth(),
//							verticalAlignment = Alignment.CenterVertically,
//							horizontalArrangement = Arrangement.Start
//						) {
//							DragSongButton(song = item)
//							HostSongItem(song = item)
//							ApproveDenyButtons(
//								song = item,
//								removeSong = removeSong,
//								setApprovalStatus = setApprovalStatus,
//							)
//							SongActions(song = item, isUpvoted = isSongUpvoted, onVoteClick = {
//								isSongUpvoted = !isSongUpvoted
//							}, roomManager = roomManager, roomCode = roomCode, isHost = isHost,
//							maxSongUpvotes = maxSongUpvotes)
//						}
//					}
//				}
//			}
//		}

		queuedSongList.forEach { song ->
			var isSongUpvoted by remember {
				mutableStateOf(false)
			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.Start
			) {
				DragSongButton(song = song)
				HostSongItem(song = song)
				ApproveDenyButtons(
					song = song,
					removeSong = removeSong,
					setApprovalStatus = setApprovalStatus,
				)
				SongActions(song = song, isUpvoted = isSongUpvoted, onVoteClick = {
					isSongUpvoted = !isSongUpvoted
				}, roomManager = roomManager, roomCode = roomCode, isHost = isHost,
				maxSongUpvotes = maxSongUpvotes)
			}
		}
	} else {
		queuedSongList.forEach { song ->
			var isSongUpvoted by remember {
				mutableStateOf(false)
			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.Start
			) {
				GuestSongItem(song = song)
				SongActions(song = song, isUpvoted = isSongUpvoted, onVoteClick = {
					isSongUpvoted = !isSongUpvoted
				}, roomManager = roomManager, roomCode = roomCode, isHost = isHost,
				maxSongUpvotes = maxSongUpvotes)
			}
		}
	}
}

@Composable
fun GuestSongItem(
	song: Song,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.fillMaxWidth(fraction = 0.8f)
	) {
		if (song.approvalStatus == ApprovalStatus.APPROVED) {
			Image(
				modifier = Modifier
					.size(30.dp)
					.clickable { /* TODO: Add tooltip explaining that host has approved*/ },
				painter = painterResource(id = R.drawable.approve_check),
				contentDescription = null
			)
		} else {
			Column(modifier = Modifier.padding(start = 30.dp)) {}
		}
		Column(modifier = Modifier
			.padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
		) {
			Text(text = song.songTitle, color = Color.White)
			Text(text = song.songArtist, color = Color.LightGray)
			Text(text = "Upvotes: " + song.votes, color = Color.LightGray)
		}
	}
}

@Composable
fun SongActions(song: Song, isUpvoted: Boolean, onVoteClick: () -> Unit, roomManager: RoomManager?, roomCode: String, maxSongUpvotes: Int, isHost: Boolean) {
	val expanded = remember { mutableStateOf(false) }
	Image(
		modifier = Modifier
			.clickable {
				if(isHost){ /*Hosts get unlimited voting*/
					/*If the user hasn't upvoted, increment upvotes by one*/
					if (!isUpvoted) {
						roomManager?.upvoteSong(roomCode, song.context_uri)
					} else { /*If user has upvoted: undo the upvote*/
						roomManager?.downvoteSong(roomCode, song.context_uri)
					}
					onVoteClick()
				}
				else{ /*Guest voting is rate limited*/
					// TODO: check if guest hasn't exceeded max upvotes.
//					val currentUpvotes : Int = roomManager.getCurrentUpvotes(roomCode, SpotifyUserToken.getToken()){currentVotes ->
//							return currentVotes
//					}
//					if(1 == maxSongUpvotes){
//
//					}
					/*If the user hasn't upvoted, increment upvotes by one*/
					if (!isUpvoted) {
						roomManager?.upvoteSong(roomCode, song.context_uri)
					} else { /*If user has upvoted: undo the upvote*/
						roomManager?.downvoteSong(roomCode, song.context_uri)
					}
					onVoteClick()
				}
			}
			.alpha(if (isUpvoted) 0.5f else 1.0f),
		painter = painterResource(id = R.drawable.upvote_arrow),
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
				text = { Text(text = "Open in Spotify") },
				onClick = { /* TODO: open in spotify */ }
			)
		}
	}
}

@Composable
fun ApproveDenyButtons(
	song : Song,
	removeSong: (Song) -> Unit = { },
	setApprovalStatus: (Song, ApprovalStatus) -> Unit,
) {
	val expanded = remember { mutableStateOf(false) }

	Row(verticalAlignment = Alignment.CenterVertically) {
		Image(
			modifier = Modifier
				.size(30.dp)
				.clickable {
					if (song.approvalStatus == ApprovalStatus.APPROVED) {
						setApprovalStatus(song, ApprovalStatus.PENDING_APPROVAL)
					} else {
						setApprovalStatus(song, ApprovalStatus.APPROVED)
					}
				}
				.padding(start = 10.dp),
			painter = if (song.approvalStatus == ApprovalStatus.APPROVED) {
				painterResource(id = R.drawable.approve_check_purple)
			} else {
				painterResource(id = R.drawable.approve_check)
			},
			contentDescription = null
		)
		Image(
			modifier = Modifier
				.size(30.dp)
				.clickable {
					if (song.approvalStatus == ApprovalStatus.DENIED) {
						setApprovalStatus(song, ApprovalStatus.PENDING_APPROVAL)
					} else {
						setApprovalStatus(song, ApprovalStatus.DENIED)
					}
				}
				.padding(start = 10.dp),
			painter = if (song.approvalStatus == ApprovalStatus.DENIED) {
				painterResource(id = R.drawable.deny_x_purple)
			} else {
				painterResource(id = R.drawable.deny_x)
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
				DropdownMenuItem(
					text = { Text(text = "Open in Spotify") },
					onClick = { /* TODO: open in spotify */ }
				)
			}
		}
	}
}

@Composable
fun HostSongItem(
	song: Song
) {
	Column(modifier = Modifier
		.fillMaxWidth(fraction = 0.7f)
		.padding(15.dp)
		.clickable { /* TODO: Redirects to spotify */ },
	) {
		Text(text = song.songTitle, color = Color.White)
		Text(text = song.songArtist, color = Color.LightGray)
		Text(text = "Upvotes: " + song.votes, color = Color.LightGray)
	}
}

@Composable
fun DragSongButton(
	song: Song
) {
	Image(
		modifier = Modifier
			.size(20.dp)
			.clickable {  },
		painter = painterResource(id = R.drawable.drag_song_icon),
		contentDescription = null
	)
	//	TODO: change clickable image to draggable
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
				playingSong = Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.APPROVED),
				queuedSongList = listOf(
					Song(songTitle = "What makes you beautifullllllllllllll", songArtist = "Shakira", approvalStatus = ApprovalStatus.APPROVED),
					Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.PENDING_APPROVAL),
					Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.PENDING_APPROVAL),
					Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.DENIED),
					Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.DENIED),
				),
				roomCode = "ABCDE",
				roomManager = null,
				appContext = LocalContext.current,
				maxSongUpvotes = 10
			)
		}
	}
}