package com.example.jukebox

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class RoomManager {
    val database = Firebase.database.reference

    fun createRoom(hostToken: String) {
        val newRoom = Room(hostToken)
        database.child(hostToken).setValue(newRoom)
    }

    fun createRoom(hostToken: String, room: Room) {
        database.child(hostToken).setValue(room)
    }

    fun deleteRoom(hostToken: String) {
        database.child(hostToken).removeValue()
    }

    fun updateRoom(hostToken: String, room: Room) {
        createRoom(hostToken, room)
    }

    fun getRoom(hostToken: String, callback: (Room?) -> Unit) {
        database.child(hostToken).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val room = dataSnapshot.getValue<Room>()
                callback(room)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    }

    fun addUserTokenToRoom(hostToken: String, userToken: String) {
        val userTokensRef = database.child("$hostToken/userTokens")
        userTokensRef.child(userToken).setValue(userToken)
    }

    fun removeUserFromRoom(hostToken: String, userToken: String) {
        val userTokensRef = database.child("$hostToken/userTokens")
        userTokensRef.child(userToken).removeValue()
    }

    fun addSongToQueue(hostToken: String, song: Song) {
        val queueRef = database.child("$hostToken/queue")
        queueRef.child(song.context_uri).setValue(song)
    }

    fun removeSongFromQueue(hostToken: String, songId: String) {
        val queueRef = database.child("$hostToken/queue")
        queueRef.child(songId).removeValue()
    }

    fun upvoteSong(hostToken: String, songId: String) {
        val voteRef = database.child("$hostToken/queue/$songId/votes")

        // Transaction code based on: https://stackoverflow.com/a/76369990
        voteRef.runTransaction(object : Transaction.Handler {

            override fun doTransaction(mutableData: MutableData): Transaction.Result {

                val value = mutableData.getValue(Int::class.java)

                if (value == null) {
                    mutableData.value = 0
                } else {
                    mutableData.value = value + 1
                }

                return Transaction.success(mutableData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {

                if (error != null) {
                    println("transaction-onCompleteError: ${error.message}")
                }

                val currentCount = currentData?.getValue(Long::class.java) ?: 0L
                println("currentCount: $currentCount")
            }
        })
    }

    // Not working, need to fix
//    fun getQueue(hostToken: String) : SongQueue {
//        val queueRef = database.child("$hostToken/queue")
//        var queue: SongQueue
//        queueRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//               queue = dataSnapshot.getValue(SongQueue::class.java)!!
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle any errors
//            }
//        })
//        return queue
//    }
}