package com.example.jukebox

class Room (val userTokens: MutableList<String>,
            val hostToken: String,
            val queue: SongQueue) {

    constructor(hostToken: String) : this(mutableListOf<String>(), hostToken, SongQueue())

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

