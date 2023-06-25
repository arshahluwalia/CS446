package com.example.jukebox

class Song(
	val songTitle: String = "",
	val songArtist: String = "",
	val isApproved: Boolean = false,
	val context_uri: String = "",
	var votes: Int = 0,
) {
//	init {
//		// TODO: Populate songTitle and Artist from context_uri
//		songTitle = ""
//		songArtist = ""
//	}
	fun upvote() {
		votes++
	}
}

