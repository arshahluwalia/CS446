package com.example.jukebox

import com.example.jukebox.spotify.task.SpotifySongControlTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking

class QueueListener {

	companion object {
		private var previousQueue: List<Song> = emptyList()
		private var currentQueue: List<Song> = emptyList()
		private var queue: MutableStateFlow<List<Song>> = MutableStateFlow(emptyList())
		private val roomManager = RoomManager()

		fun setQueueFlow(newQueue: MutableStateFlow<List<Song>>) {
			queue = newQueue
		}

		suspend fun onQueueChanged(roomCode: String, uTokens: MutableStateFlow<MutableList<String>>) {
			queue.collectLatest {
				if (previousQueue.isEmpty()) {
					if (it.isNotEmpty()) {
						// play song
						val currentSong = runBlocking { roomManager.getCurrentSong(roomCode) }
						if (currentSong != null) {
							SpotifySongControlTask.playSong(
								currentSong.context_uri,
								0,
								uTokens.value
							)
						}
					}
				}
				val tempQueue = currentQueue
				currentQueue = it
				previousQueue = tempQueue
			}
		}
	}
}