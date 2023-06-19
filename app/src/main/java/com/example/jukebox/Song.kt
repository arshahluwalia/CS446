package com.example.jukebox
class Song(val songTitle: String,
		   val songArtist: String,
		   val isApproved: Boolean,
		   val context_uri: String,
		   var votes: Int) {
	constructor(context_uri: String) : this("", "", false, context_uri, 0)
	constructor(context_uri: String, votes: Int) : this("", "", false, context_uri, votes)
	constructor(songTitle: String, songArtist: String, isApproved: Boolean) : this(songTitle, songArtist, isApproved, "", 0)
	fun upvote() {
		votes++
	}
}

