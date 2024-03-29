package com.example.jukebox.songqueue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.jukebox.ApprovalStatus
import com.example.jukebox.CurrentSong
import com.example.jukebox.QueueListener
import com.example.jukebox.RoomManager
import com.example.jukebox.SecondaryBackground
import com.example.jukebox.Song
import com.example.jukebox.spotify.SpotifyUserToken
import com.example.jukebox.ui.theme.JukeboxTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private lateinit var roomCode : String

class GuestSongQueueActivity  : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomCode = intent.getStringExtra("roomCode").toString()
        val songQueue = MutableStateFlow<List<Song>>(emptyList())
        val approvedSongQueue = MutableStateFlow<List<Song>>(emptyList())
        val deniedSongQueue = MutableStateFlow<List<Song>>(emptyList())
        getSongQueueByOrderAdded(roomCode, songQueue)
        getApprovedSongQueueByHostOrder(roomCode, approvedSongQueue)
        getDeniedSongQueue(roomCode, deniedSongQueue)
        val hostName = MutableStateFlow("")
        getHostName(roomCode, hostName)
        val roomManager = RoomManager()
        val maxSongUpvotes = MutableStateFlow(0)
        getMaxUpvotesAllowed(roomCode, roomManager, maxSongUpvotes)
        val appContext = applicationContext
        val dispatcher = onBackPressedDispatcher
        val hostToken = MutableStateFlow("")
        val userTokens = MutableStateFlow<MutableList<String>>(ArrayList())
        getHostToken(roomCode, hostToken, roomManager)
        getUserTokens(roomCode, userTokens, roomManager, hostToken)
        getCurrentSongInfo(roomCode, roomManager)
        QueueListener.setQueueFlow(approvedSongQueue)
        CurrentSong.setInitialVars(roomCode, userTokens, false)

        val myScope = CoroutineScope(Dispatchers.Main)
        myScope.launch {
            QueueListener.onQueueChanged(roomCode, userTokens)
        }
        myScope.launch {
            CurrentSong.onDurationChanged()
        }
        myScope.launch {
            CurrentSong.onSongChanged()
        }
        myScope.launch {
            CurrentSong.onTimerFinishes()
        }

        setContent {
            // TODO: need to retrieve current song instead of hardcoding
            val concatSongQueue =
                approvedSongQueue.collectAsState().value + songQueue.collectAsState().value + deniedSongQueue.collectAsState().value
            val currentApprovedSongs: List<Song> = approvedSongQueue.collectAsState().value
            JukeboxTheme() {
                SongQueueScreenContent(
                    dispatcher = dispatcher,
                    hostName = hostName.collectAsState().value,
                    isHost = false,
                    playingSong =
                        if (currentApprovedSongs.isEmpty()) Song()
                        else currentApprovedSongs[0],
                    queuedSongList = concatSongQueue,
                    roomCode = roomCode,
                    roomManager = roomManager,
                    appContext = appContext,
                    remainingUpvotes = maxSongUpvotes
                )
            }
        }
    }

    private fun getSongQueue(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        val roomManager = RoomManager()
        roomManager.getPendingQueue(roomCode) { queue ->
            songQueue.value = queue.queue
        }
    }

    private fun getSongQueueByOrderAdded(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        val roomManager = RoomManager()
        // update the songqueue, ordered by the timestamp by which it was added
        roomManager.getPendingQueue(roomCode) { queue ->
            songQueue.value = queue.queue.sortedBy { it.timeStampAdded }
        }
    }

    private fun getApprovedSongQueueByHostOrder(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        val roomManager = RoomManager()
        roomManager.getApprovedQueueCallback(roomCode) { queue ->
            songQueue.value = queue.queue.sortedBy { it.hostOrder }
        }
    }

    private fun getApprovedSongQueue(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        val roomManager = RoomManager()
        roomManager.getApprovedQueueCallback(roomCode) { queue ->
            songQueue.value = queue.queue
        }
    }

    private fun getApprovedSongQueueByOrderAdded(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        val roomManager = RoomManager()
        // update the songqueue, ordered by the timestamp by which it was added
        roomManager.getApprovedQueueCallback(roomCode) { queue ->
            songQueue.value = queue.queue.sortedBy { it.timeStampAdded }
        }
    }

    private fun getDeniedSongQueue(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        val roomManager = RoomManager()
        roomManager.getDeniedQueueCallback(roomCode) { queue ->
            songQueue.value = queue.queue
        }
    }

    private fun getDeniedSongQueueByOrderAdded(roomCode: String, songQueue: MutableStateFlow<List<Song>>) {
        val roomManager = RoomManager()
        // update the songqueue, ordered by the timestamp by which it was added
        roomManager.getDeniedQueueCallback(roomCode) { queue ->
            songQueue.value = queue.queue.sortedBy { it.timeStampAdded }
        }
    }

    private fun getHostName(roomCode: String, hostName: MutableStateFlow<String>) {
        val roomManager = RoomManager()
        roomManager.getHostName(roomCode) { name ->
            hostName.value = name
        }
    }

    private fun getMaxUpvotes(roomCode: String, maxUpvotes: MutableStateFlow<Int>) {
        val roomManager = RoomManager()
        roomManager.getMaxUpvotes(roomCode) { max ->
            maxUpvotes.value = max
        }
    }

    private fun getMaxUpvotesAllowed(
        roomCode: String,
        roomManager: RoomManager?,
        maxUpvotesAllowed: MutableStateFlow<Int>
    ) {
        roomManager?.getMaxUpvotes(roomCode) { maxUpvotes ->
            roomManager.getCurrentUpvotes(roomCode, SpotifyUserToken.getToken()) { currentUpvotes ->
                if (maxUpvotes - currentUpvotes < 0) {
                    maxUpvotesAllowed.value = 0
                } else {
                    maxUpvotesAllowed.value = maxUpvotes - currentUpvotes
                }
            }
        }
    }

    private fun getHostToken(
        roomCode: String,
        hostToken: MutableStateFlow<String>,
        roomManager: RoomManager?
    ) {
        roomManager?.getHostToken(roomCode) { token ->
            hostToken.value = token
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

    private fun getCurrentSongInfo(
        roomCode: String,
        roomManager: RoomManager?
    ) {
        roomManager?.getSongDurationCallback(roomCode) {
            if (it != 0) CurrentSong.setDuration(it)
        }
        roomManager?.getCurrentSongCallback(roomCode) {
            CurrentSong.setCurrentSong(it)
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
                playingSong = Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.APPROVED),
                queuedSongList = listOf(
                    Song(songTitle = "Hips Don't LieHips Don't LieHips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.APPROVED),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.PENDING_APPROVAL),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.PENDING_APPROVAL),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.DENIED),
                    Song(songTitle = "Hips Don't Lie", songArtist = "Shakira", approvalStatus = ApprovalStatus.DENIED),
                ),
                roomCode = "ABCDE",
                roomManager = null,
                appContext = LocalContext.current,
                remainingUpvotes = MutableStateFlow(10)
            )
        }
    }
}
