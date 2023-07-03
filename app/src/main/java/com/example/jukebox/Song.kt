package com.example.jukebox

class Song(
	val context_uri: String = "",
	val songTitle: String = "",
	val songArtist: String = "",
	val approvalStatus: ApprovalStatus = ApprovalStatus.PENDING_APPROVAL,
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

	fun downvote(){
		votes--
	}
}

enum class ApprovalStatus {
	PENDING_APPROVAL,
	APPROVED,
	DENIED
}

