package com.example.jukebox

import java.time.Duration

class Song(
	val context_uri: String = "",
	val songTitle: String = "",
	val songArtist: String = "",
	val approvalStatus: ApprovalStatus = ApprovalStatus.PENDING_APPROVAL,
	var votes: Int = 0,
	var duration: Int = 0
) {
//	init {
//		// TODO: Populate songTitle, Artist and duration from context_uri
//		songTitle = ""
//		songArtist = ""
	// 	duration =
//	}
	fun upvote() {
		votes++
	}

	fun downvote(){
		votes--
	}
}

enum class ApprovalStatus {
	PENDING_APPROVAL,
	APPROVED,
	DENIED
}

