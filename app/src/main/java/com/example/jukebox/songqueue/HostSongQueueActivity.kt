package com.example.jukebox.songqueue

import android.app.Activity
import android.content.Context
import android.os.Bundle
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jukebox.ApprovalStatus
import com.example.jukebox.QueueListener
import com.example.jukebox.R
import com.example.jukebox.Room
import com.example.jukebox.RoomManager
import com.example.jukebox.RoomStore
import com.example.jukebox.SecondaryBackground
import com.example.jukebox.Song
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.util.HideSoftKeyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private lateinit var roomCode : String

class HostSongQueueActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomCode = intent.getStringExtra("roomCode").toString()
        val isReturning = intent.getBooleanExtra("isReturning", false)
        val songQueue = MutableStateFlow<List<Song>>(emptyList())
        val approvedSongQueue = MutableStateFlow<List<Song>>(emptyList())
        val deniedSongQueue = MutableStateFlow<List<Song>>(emptyList())
        getSongQueue(roomCode, songQueue)
        getApprovedSongQueue(roomCode, approvedSongQueue)
        getDeniedSongQueue(roomCode, deniedSongQueue)
        val hostName = MutableStateFlow("")
        getHostName(roomCode, hostName)
        val roomManager = RoomManager()
        val appContext = applicationContext
        val dispatcher = onBackPressedDispatcher
        RoomStore.setMostRecentRoom(Room(roomCode = roomCode))
        val hostToken = MutableStateFlow("")
        val userTokens = MutableStateFlow<MutableList<String>>(ArrayList())
        getHostToken(roomCode, hostToken, userTokens, roomManager)
        getUserTokens(roomCode, userTokens, roomManager, hostToken)
        QueueListener.setQueueFlow(approvedSongQueue)

        val myScope = CoroutineScope(Dispatchers.Main)
        myScope.launch {
            withContext(Dispatchers.IO) {
                QueueListener.onQueueChanged(roomCode, userTokens)
            }
        }
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController,
                startDestination = if (isReturning) "songqueue/$hostName" else "entername"
            ) {
                composable("entername") {
                    EnterName(
                        dispatcher = dispatcher,
                        navController = navController,
                        activity = this@HostSongQueueActivity,
                        roomManager = roomManager
                    )
                }
                composable(
                    "songqueue/{hostName}",
                    arguments = listOf(navArgument("hostName") { type = NavType.StringType })
                ) {backStackEntry ->
                    SongQueue(
                        dispatcher = dispatcher,
                        hostName = hostName.collectAsState().value,
                        songQueue = songQueue,
                        approvedSongQueue = approvedSongQueue,
                        deniedSongQueue = deniedSongQueue,
                        removeSong = ::removeSong,
                        roomManager = roomManager,
                        appContext = appContext,
                        setApprovalStatus = ::setApprovalStatus,
                        hostToken = hostToken,
                        userTokens = userTokens
                    )
                }
            }
        }
    }

    private fun getSongQueue(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        val roomManager = RoomManager()
        roomManager.getPendingQueue(roomCode) { queue ->
            songQueue.value = queue.queue
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

    private fun removeSong(song: Song) {
        val roomManager = RoomManager()
        roomManager.removeSongFromPendingQueue(roomCode, song.context_uri)
    }

    private fun getHostName(roomCode: String, hostName: MutableStateFlow<String>) {
        val roomManager = RoomManager()
        roomManager.getHostName(roomCode) { name ->
            hostName.value = name
        }
    }

    private fun setApprovalStatus(song: Song, approvalStatus: ApprovalStatus) {
        val roomManager = RoomManager()
        roomManager.setSongApprovalStatus(roomCode, song, approvalStatus)
    }

    private fun getHostToken(
        roomCode: String,
        hostToken: MutableStateFlow<String>,
        userTokens: MutableStateFlow<MutableList<String>>,
        roomManager: RoomManager?
    ) {
        roomManager?.getHostToken(roomCode) { token ->
            hostToken.value = token
            //getUserTokens(roomCode, userTokens, roomManager, hostToken)
        }
    }

    private fun getUserTokens(
        roomCode: String,
        userTokens: MutableStateFlow<MutableList<String>>,
        roomManager: RoomManager?,
        hostToken: MutableStateFlow<String>
    ) {
        roomManager?.getUsers(roomCode) { users ->
            val tokens = mutableListOf<String>()
            users.forEach { tokens.add(it.userToken) }
            userTokens.value = tokens
            userTokens.value.add(hostToken.value)
        }
    }
}

@Composable
private fun EnterName(
    dispatcher: OnBackPressedDispatcher? = null,
    navController: NavController?,
    activity: Activity?,
    roomManager: RoomManager?
) {
    var hostName by remember { mutableStateOf("") }

    JukeboxTheme {
        Box {
            SecondaryBackground()
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 200.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    BackButton(dispatcher)
                }
                Text(
                    textAlign = TextAlign.Center,
                    text = "Enter your name here!",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                )
                Text(
                    textAlign = TextAlign.Center,
                    text = "This is what the guests will see.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                )
                TextField(
                    modifier = Modifier.padding(vertical = 20.dp),
                    value = hostName,
                    onValueChange = {
                        hostName = it
                    },
                    label = {
                        Text(
                            text = "Enter your name",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    shape = RoundedCornerShape(20),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (activity != null) {
                                HideSoftKeyboard.hideSoftKeyboard(activity = activity)
                            }
                            navController?.navigate("songqueue/$hostName")
                            roomManager?.setHostName(roomCode, hostName)
                        }
                    )
                )
                Button(
                    onClick = {
                        if (activity != null) {
                            HideSoftKeyboard.hideSoftKeyboard(activity = activity)
                        }
                        navController?.navigate("songqueue/$hostName")
                        roomManager?.setHostName(roomCode, hostName)
                    },
                    enabled = hostName.isNotEmpty()
                ) {
                    Text(text = "Done")
                }
            }
        }
    }
}

@Composable
private fun BackButton(dispatcher: OnBackPressedDispatcher? = null) {
    TextButton(
        onClick = { dispatcher?.onBackPressed() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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
private fun SongQueue(
    dispatcher: OnBackPressedDispatcher? = null,
    hostName: String?,
    songQueue: MutableStateFlow<List<Song>>,
    approvedSongQueue: MutableStateFlow<List<Song>>,
    deniedSongQueue: MutableStateFlow<List<Song>>,
    removeSong: (Song) -> Unit = { },
    roomManager: RoomManager,
    appContext: Context,
    setApprovalStatus: (Song, ApprovalStatus) -> Unit,
    hostToken: MutableStateFlow<String>,
    userTokens: MutableStateFlow<MutableList<String>>
) {
    val concatSongQueue =
        approvedSongQueue.collectAsState().value + songQueue.collectAsState().value + deniedSongQueue.collectAsState().value

    val mutableSongQueue = MutableStateFlow(concatSongQueue)
    JukeboxTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)) {
            Image(painter = painterResource(id = R.drawable.secondary_background), contentDescription = null)
            SongQueueScreenContent(
                dispatcher = dispatcher,
                hostName = hostName ?: "You",
                isHost = true,
                playingSong =
                    if (approvedSongQueue.collectAsState().value.isEmpty()) Song()
                    else approvedSongQueue.collectAsState().value[0],
                queuedSongList = concatSongQueue,
                roomCode = roomCode,
                removeSong = removeSong,
                roomManager = roomManager,
                appContext = appContext,
                setApprovalStatus = setApprovalStatus,
                maxSongUpvotes = 999999,
                hostToken = hostToken,
                userTokens = userTokens,
                mutableSongList = mutableSongQueue
            )
        }
    }
}

@Preview
@Composable
private fun PreviewEnterNameScreenContent() {
    EnterName(
        navController = null,
        activity = null,
        roomManager = null
    )
}

@Preview
@Composable
private fun PreviewHostQueueScreenContent() {
    JukeboxTheme {
        SecondaryBackground()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SongQueueScreenContent(
                hostName = "Lucas",
                isHost = true,
                playingSong = Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.APPROVED),
                queuedSongList = listOf(
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.APPROVED),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.PENDING_APPROVAL),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.PENDING_APPROVAL),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.DENIED),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.DENIED),
                ),
                roomCode = "ABCDE",
                roomManager = null,
                appContext = LocalContext.current,
                maxSongUpvotes = 999999
            )
        }
    }
}