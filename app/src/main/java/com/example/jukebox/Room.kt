package com.example.jukebox

class Room (
    val roomCode: String,
    val hostToken: String = "",
    val users: MutableList<User> = mutableListOf(),
    val queue: SongQueue = SongQueue(),
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

    fun addSongToQueue(song: Song) {
        queue.addSong(song)
    }

    fun addSongToQueueByContextUri(contextUri: String) {
        queue.addSongByContextUri(contextUri)
    }
}

