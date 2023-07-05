package com.example.jukebox

class Room (
    val roomCode: String,
    val hostToken: String = "",
    val users: MutableList<User> = mutableListOf(),
    val pendingQueue: SongQueue = SongQueue(),
    val approvedQueue: SongQueue = SongQueue(),
    val deniedQueue: SongQueue = SongQueue(),
    val maxUpvotes: Int = 5,
    val maxSuggestions: Int = 5,
    val hostName: String = ""
) {

    private fun isHostInitialized(): Boolean {
        return hostToken != ""
    }
    fun addUser(user: User) {
        users.add(user)
    }

    fun removeUser(user: User) {
        users.remove(user)
    }

    fun addSongToApprovedQueue(song: Song) {
        approvedQueue.addSong(song)
    }

    fun addSongToPendingQueue(song: Song) {
        pendingQueue.addSong(song)
    }

    fun addSongToDeniedQueue(song: Song) {
        deniedQueue.addSong(song)
    }

    fun addSongToPendingQueueByContextUri(contextUri: String) {
        pendingQueue.addSongByContextUri(contextUri)
    }

    fun addSongToApprovedQueueByContextUri(contextUri: String) {
        approvedQueue.addSongByContextUri(contextUri)
    }

    fun addSongToDeniedQueueByContextUri(contextUri: String) {
        deniedQueue.addSongByContextUri(contextUri)
    }
}

