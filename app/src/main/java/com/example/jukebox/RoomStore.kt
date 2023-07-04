package com.example.jukebox

import kotlinx.coroutines.flow.MutableStateFlow

class RoomStore {
	companion object {
		private var mostRecentRoom: Room? = null
		private var hasRecentRoom = MutableStateFlow(false)

		fun hasRecentRoom() = hasRecentRoom
		fun getMostRecentRoom() = mostRecentRoom
		fun setMostRecentRoom(room: Room) {
			mostRecentRoom = room
			hasRecentRoom.value = true
		}
	}
}