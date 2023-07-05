package com.example.jukebox

class SongQueue (public val queue: MutableList<Song> = mutableListOf()) {
    constructor(songs: Collection<Song>) : this(songs.toMutableList())

    fun addSong(song: Song) {
        queue.add(song)
    }

    fun addSongByContextUri(contextUri: String) {
        val song = Song(contextUri)
        queue.add(song)
    }

    fun removeSong(song: Song) {
        queue.remove(song)
    }

    fun upvoteSong(song: Song) {
        song.upvote()
    }

    fun getNextSong(): Song? {
        return queue.firstOrNull()
    }

    fun checkEmpty(): Boolean {
        return queue.isEmpty()
    }

    fun clearQueue() {
        queue.clear()
    }
    fun getPrev(): Song{
        var last = queue.size
        return queue.get(last-1)
    }
    fun getNext(): Song{
        return queue.get(0)
    }
}