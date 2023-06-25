package com.example.jukebox

class Room (val roomCode: String,
            val hostToken: String = "",
            val userTokens: MutableList<String> = mutableListOf<String>(),
            val queue: SongQueue = SongQueue()) {

    private fun isHostInitialized(): Boolean {
        return hostToken != ""
    }
    fun addUser(userToken: String) {
        userTokens.add(userToken)
    }

    fun removeUser(userToken: String) {
        userTokens.remove(userToken)
    }

    fun addSongToQueue(song: Song) {
        queue.addSong(song)
    }

    fun addSongToQueueByContextUri(contextUri: String) {
        queue.addSongByContextUri(contextUri)
    }
}

