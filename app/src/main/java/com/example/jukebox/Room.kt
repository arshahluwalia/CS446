package com.example.jukebox

class Room (val hostToken: String,
            val userTokens: MutableList<String> = mutableListOf<String>(),
            val queue: SongQueue = SongQueue()) {

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

