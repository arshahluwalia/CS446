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

    fun createRoom(roomCode: String) {
        val newRoom = Room(roomCode)
        database.child(roomCode).setValue(newRoom)
    }

    fun createRoom(roomCode: String, room: Room) {
        database.child(roomCode).setValue(room)
    }

    fun deleteRoom(roomCode: String) {
        database.child(roomCode).removeValue()
    }

    fun updateRoom(roomCode: String, room: Room) {
        createRoom(roomCode, room)
    }

    fun getRoom(roomCode: String, callback: (Room?) -> Unit) {
        database.child(roomCode).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val room = dataSnapshot.getValue<Room>()
                callback(room)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    }

    fun addUserTokenToRoom(roomCode: String, userToken: String) {
        val userTokensRef = database.child("$roomCode/userTokens")
        userTokensRef.child(userToken).setValue(userToken)
    }

    fun removeUserFromRoom(roomCode: String, userToken: String) {
        val userTokensRef = database.child("$roomCode/userTokens")
        userTokensRef.child(userToken).removeValue()
    }

    fun addSongToQueue(roomCode: String, song: Song) {
        val queueRef = database.child("$roomCode/queue")
        queueRef.child(song.context_uri).setValue(song)
    }

    fun removeSongFromQueue(roomCode: String, songId: String) {
        val queueRef = database.child("$roomCode/queue")
        queueRef.child(songId).removeValue()
    }

    fun upvoteSong(roomCode: String, songId: String) {
        val voteRef = database.child("$roomCode/queue/$songId/votes")

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
    fun checkRoomExists(roomCode: String) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(roomCode)) {
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    fun setHostToken(roomCode: String, hostToken: String) {
        val hostTokenRef = database.child("$roomCode/hostToken")
        hostTokenRef.setValue(hostToken)
    }

    fun setHostName(roomCode: String, name: String) {
        val hostNameRef = database.child("$roomCode/hostName")
        hostNameRef.setValue(name)
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