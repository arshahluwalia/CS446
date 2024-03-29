package com.example.jukebox

import android.util.Log
import com.example.jukebox.spotify.SpotifyUserToken
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class RoomManager {
    private val database = Firebase.database.reference

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

    fun addUserToRoom(roomCode: String, user: User) {
        val userRef = database.child("$roomCode/users")
        userRef.child(user.userToken).setValue(user)
    }

    fun removeUserFromRoom(roomCode: String, userToken: String) {
        val userRef = database.child("$roomCode/users")
        userRef.child(userToken).removeValue()
    }

    fun addSongToPendingQueue(roomCode: String, song: Song) {
        val queueRef = database.child("$roomCode/pendingQueue")
        queueRef.child(song.context_uri).setValue(song)
    }
    fun addSongToApprovedQueue(roomCode: String, song: Song, priority: Number = 0) {
        val queueRef = database.child("$roomCode/approvedQueue")
        queueRef.child(song.context_uri).setValue(song)
    }

    fun addSongToDeniedQueue(roomCode: String, song: Song) {
        val queueRef = database.child("$roomCode/deniedQueue")
        queueRef.child(song.context_uri).setValue(song)
    }

    fun removeSongFromPendingQueue(roomCode: String, songId: String) {
        val queueRef = database.child("$roomCode/pendingQueue")
        queueRef.child(songId).removeValue()
    }

    fun removeSongFromApprovedQueue(roomCode: String, songId: String) {
        val queueRef = database.child("$roomCode/approvedQueue")
        if (CurrentSong.isHost) {
            queueRef.child(songId).removeValue()
        }
    }

    fun removeSongFromDeniedQueue(roomCode: String, songId: String) {
        val queueRef = database.child("$roomCode/deniedQueue")
        queueRef.child(songId).removeValue()
    }

    fun swapSongs(roomCode: String, upperSong: Song, lowerSong: Song) {
        val queueRef = database.child("$roomCode/approvedQueue")
        queueRef.child(upperSong.context_uri).child("hostOrder").setValue(lowerSong.hostOrder)
        queueRef.child(lowerSong.context_uri).child("hostOrder").setValue(upperSong.hostOrder)
    }

    fun setSongHostOrder(roomCode: String, song: Song, hostOrderValue: Int) {
        val queueRef = database.child("$roomCode/approvedQueue/${song.context_uri}/hostOrder")
        queueRef.setValue(hostOrderValue)
    }

    fun setPreviousSongHostOrder(roomCode: String, song: Song, hostOrderValue: Int) {
        val queueRef = database.child("$roomCode/previousQueue/${song.context_uri}/hostOrder")
        queueRef.setValue(hostOrderValue)
    }

    fun setPendingSongHostOrder(roomCode:String, song: Song) {
        val queueRef = database.child("$roomCode/pendingQueue/${song.context_uri}/hostOrder")
        queueRef.setValue(-1)
    }

    fun setDeniedSongHostOrder(roomCode:String, song: Song) {
        val queueRef = database.child("$roomCode/deniedQueue/${song.context_uri}/hostOrder")
        queueRef.setValue(-1)
    }

    suspend fun addSongToPreviousQueue(roomCode: String, song: Song?) {
        val queueRef = database.child("$roomCode/previousQueue/${song?.context_uri}")

        val previousSongsList = getPreviousQueue(roomCode)
        if (previousSongsList != null) {
            for (previousSong in previousSongsList) {
                setPreviousSongHostOrder(roomCode, previousSong, previousSong.hostOrder + 1)
            }
        }

        queueRef.setValue(song)
    }

    suspend fun addSongToApprovedQueueHead(roomCode: String, song: Song?) {
        val queueRef = database.child("$roomCode/approvedQueue/${song?.context_uri}")

        val approvedSongsList = getApprovedQueueAsList(roomCode)
        if (approvedSongsList != null) {
            for (approvedSong in approvedSongsList) {
                setSongHostOrder(roomCode, approvedSong, approvedSong.hostOrder + 1)
            }
        }

        queueRef.setValue(song)
    }

    fun removeSongFromApprovedQueue(roomCode: String, song: Song?) {
        val queueRef = database.child("$roomCode/approvedQueue/${song?.context_uri}")
        if (CurrentSong.isHost) {
            queueRef.removeValue()
        }
    }

    fun removeSongFromPreviousQueue(roomCode: String, song: Song?) {
        val queueRef = database.child("$roomCode/previousQueue/${song?.context_uri}")
        queueRef.removeValue()
    }

    fun setSongApprovalStatus(roomCode: String, song: Song, approvalStatus: ApprovalStatus) {
        val context_uri = song.context_uri
        var approvalRef = when(approvalStatus) {
            ApprovalStatus.APPROVED -> {
                database.child("$roomCode/approvedQueue/${song.context_uri}/approvalStatus")
            }
            ApprovalStatus.PENDING_APPROVAL -> {
                database.child("$roomCode/pendingQueue/${song.context_uri}/approvalStatus")
            }
            ApprovalStatus.DENIED -> {
                database.child("$roomCode/deniedQueue/${song.context_uri}/approvalStatus")
            }
        }

        val votes = runBlocking { fetchVotes(roomCode, context_uri) }
        if (votes != null) {
            song.votes = votes
        }
        if (CurrentSong.isHost) {
            if (approvalStatus == ApprovalStatus.PENDING_APPROVAL) {
                removeSongFromApprovedQueue(roomCode, context_uri)
                removeSongFromDeniedQueue(roomCode, context_uri)
                addSongToPendingQueue(roomCode, song)
            } else if (approvalStatus == ApprovalStatus.APPROVED) {
                removeSongFromDeniedQueue(roomCode, context_uri)
                removeSongFromPendingQueue(roomCode, context_uri)
                addSongToApprovedQueue(roomCode, song)
            } else {
                removeSongFromApprovedQueue(roomCode, context_uri)
                removeSongFromPendingQueue(roomCode, context_uri)
                addSongToDeniedQueue(roomCode, song)
            }
        }

        approvalRef.runTransaction(
            object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {

                    mutableData.value = approvalStatus.toString()

                    return Transaction.success(mutableData)
                }

                override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                    if (error != null) {
                        println("transaction-onCompleteError: ${error.message}")
                    }

                    val currentApprovalStatus = currentData?.getValue(String::class.java) ?: "null"
                    println("currentApprovalStatus: $currentApprovalStatus")
                }
            }
        )
    }

    fun upvoteSong(roomCode: String, songId: String, userToken: String = "") {
        // null string userToken indicates that host has upvoted
        // Change number of votes for a particular song
        val voteRef = database.child("$roomCode/pendingQueue/$songId/votes")

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

        if(userToken != ""){ // guest is upvoting
            val userVoteRef = database.child("$roomCode/users/$userToken/numUpvotes")

            userVoteRef.runTransaction(object : Transaction.Handler {

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
    }

    fun downvoteSong(roomCode: String, songId: String, userToken: String = "") {
        val voteRef = database.child("$roomCode/pendingQueue/$songId/votes")

        // Transaction code based on: https://stackoverflow.com/a/76369990
        voteRef.runTransaction(object : Transaction.Handler {

            override fun doTransaction(mutableData: MutableData): Transaction.Result {

                val value = mutableData.getValue(Int::class.java)

                if (value == null) {
                    mutableData.value = 0
                } else {
                    mutableData.value = value - 1
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

        if(userToken != ""){ // guest is undoing upvote
            val userVoteRef = database.child("$roomCode/users/$userToken/numUpvotes")

            userVoteRef.runTransaction(object : Transaction.Handler {

                override fun doTransaction(mutableData: MutableData): Transaction.Result {

                    val value = mutableData.getValue(Int::class.java)

                    if (value == null) {
                        mutableData.value = 0
                    } else {
                        mutableData.value = value - 1
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
    }

    suspend fun fetchVotes(roomCode: String, songId: String): Int? {
        val queues = listOf("pendingQueue", "deniedQueue", "approvedQueue")
        for (queue in queues) {
            val voteRef = database.child("$roomCode/$queue/$songId/votes")

            try {
                val snapshot = voteRef.get().await()
                val votes = snapshot.getValue(Int::class.java)
                // Handle the retrieved votes value
                if (votes != null && votes != 0) {
                    // Votes found, return the value
                    return votes
                }
            } catch (exception: Exception) {
                // Handle any error that occurred while fetching the votes
            }
        }

        return 0
    }

    fun setHostToken(roomCode: String, hostToken: String) {
        val hostTokenRef = database.child("$roomCode/hostToken")
        hostTokenRef.setValue(hostToken)
    }

    fun getHostToken(roomCode: String, callback: (String) -> Unit) {
        val hostTokenRef = database.child("$roomCode/hostToken")

        hostTokenRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hostToken = dataSnapshot.getValue(String::class.java)
                callback(hostToken ?: "none")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(String()) // Invoke the callback with an empty list to indicate an error or cancellation
            }
        })
    }

    fun setHostName(roomCode: String, name: String) {
        val hostNameRef = database.child("$roomCode/hostName")
        hostNameRef.setValue(name)
    }

    fun getHostName(roomCode: String, callback: (String) -> Unit) {
        val hostNameRef = database.child("$roomCode/hostName")

        hostNameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hostName = dataSnapshot.getValue(String::class.java)
                callback(hostName ?: "Someone")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(String()) // Invoke the callback with an empty SongQueue to indicate an error or cancellation
            }
        })
    }

    fun setLimitUpvotes(roomCode: String, limitUpvotes: Boolean) {
        val limitUpvotesRef = database.child("$roomCode/limitUpvotes")
        limitUpvotesRef.setValue(limitUpvotes)
    }

    fun getLimitUpvotes(roomCode: String, callback: (Boolean) -> Unit) {
        val limitUpvotesRef = database.child("$roomCode/limitUpvotes")

        limitUpvotesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val limitUpvotes = dataSnapshot.getValue(Boolean::class.java)
                callback(limitUpvotes ?: true)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(true)
            }
        })
    }

    fun setMaxUpvotes(roomCode: String, maxUpvotes: Int) {
        val maxUpvotesRef = database.child("$roomCode/maxUpvotes")
        maxUpvotesRef.setValue(maxUpvotes)
    }

    fun getMaxUpvotes(roomCode: String, callback: (Int) -> Unit) {
        val maxUpvotesRef = database.child("$roomCode/maxUpvotes")

        maxUpvotesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val maxUpvotes = dataSnapshot.getValue(Int::class.java)
                callback(maxUpvotes ?: 5)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(5)
            }
        })
    }

    fun setLimitSuggestions(roomCode: String, limitSuggestions: Boolean) {
        val limitSuggestionsRef = database.child("$roomCode/limitSuggestions")
        limitSuggestionsRef.setValue(limitSuggestions)
    }

    fun getLimitSuggestions(roomCode: String, callback: (Boolean) -> Unit) {
        val limitSuggestionsRef = database.child("$roomCode/limitSuggestions")

        limitSuggestionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val limitSuggestions = dataSnapshot.getValue(Boolean::class.java)
                callback(limitSuggestions ?: true)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(true)
            }
        })
    }


    fun setMaxSuggestions(roomCode: String, maxSuggestions: Int) {
        val maxSuggestionsRef = database.child("$roomCode/maxSuggestions")
        maxSuggestionsRef.setValue(maxSuggestions)
    }

    fun getMaxSuggestions(roomCode: String, callback: (Int) -> Unit) {
        val maxSuggestionsRef = database.child("$roomCode/maxSuggestions")

        maxSuggestionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val maxSuggestions = dataSnapshot.getValue(Int::class.java)
                callback(maxSuggestions ?: 5)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(5)
            }
        })
    }

    fun setAutoRemove(roomCode: String, autoRemove: Boolean) {
        val autoRemoveRef = database.child("$roomCode/autoRemove")
        autoRemoveRef.setValue(autoRemove)
    }

    fun getAutoRemove(roomCode: String, callback: (Boolean) -> Unit) {
        val autoRemoveRef = database.child("$roomCode/autoRemove")

        autoRemoveRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val autoRemove = dataSnapshot.getValue(Boolean::class.java)
                callback(autoRemove ?: true)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(true)
            }
        })
    }


    fun suggestSong(roomCode: String, userToken: String) {
        val userRef = database.child("$roomCode/users/$userToken/numSuggestions")

        userRef.runTransaction(object : Transaction.Handler {

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

    fun getCurrentSuggestions(roomCode: String, userToken: String, callback: (Int) -> Unit) {
        val userRef = database.child("$roomCode/users/$userToken/numSuggestions")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentSuggestions = dataSnapshot.getValue(Int::class.java)
                callback(currentSuggestions ?: 0)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(0)
            }
        })
    }

    // get how many times user has upvoted
    fun getCurrentUpvotes(roomCode: String, userToken: String, callback: (Int) -> Unit) {
        val userRef = database.child("$roomCode/users/$userToken/numUpvotes")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentUpvotes = dataSnapshot.getValue(Int::class.java)
                callback(currentUpvotes ?: 0)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(0)
            }
        })
    }

    fun checkRoomExists(inputRoom: String, callback: (Boolean) -> Unit) {
        var roomCodeExists = false

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    if (inputRoom == snapshot.key) {
                        roomCodeExists = true
                    }
                }
                callback(roomCodeExists)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(roomCodeExists)
            }
        })
    }

    fun getPendingQueue(roomCode: String, callback: (SongQueue) -> Unit) {
        val queueRef = database.child("$roomCode/pendingQueue")

        queueRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val songs = mutableListOf<Song>()
                for (snapshot in dataSnapshot.children) {
                    val song = snapshot.getValue(Song::class.java)
                    song?.let { songs.add(it) }
                }
                val songQueue = SongQueue(songs)
                callback(songQueue)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(SongQueue()) // Invoke the callback with an empty SongQueue to indicate an error or cancellation
            }
        })
    }

    fun getApprovedQueueCallback(roomCode: String, callback: (SongQueue) -> Unit) {
        val queueRef = database.child("$roomCode/approvedQueue")

        queueRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val songs = mutableListOf<Song>()
                for (snapshot in dataSnapshot.children) {
                    val song = snapshot.getValue(Song::class.java)
                    song?.let { songs.add(it) }
                }
                val songQueue = SongQueue(songs)
                callback(songQueue)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(SongQueue()) // Invoke the callback with an empty SongQueue to indicate an error or cancellation
            }
        })
    }

    fun getDeniedQueueCallback(roomCode: String, callback: (SongQueue) -> Unit) {
        val queueRef = database.child("$roomCode/deniedQueue")

        queueRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val songs = mutableListOf<Song>()
                for (snapshot in dataSnapshot.children) {
                    val song = snapshot.getValue(Song::class.java)
                    song?.let { songs.add(it) }
                }
                val songQueue = SongQueue(songs)
                callback(songQueue)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(SongQueue()) // Invoke the callback with an empty SongQueue to indicate an error or cancellation
            }
        })
    }

    suspend fun getApprovedQueue(roomCode: String): SongQueue? {
        val queueRef = database.child("$roomCode/approvedQueue")
        return try {
            val dataSnapshot = queueRef.get().await()
            val songs = mutableListOf<Song>()
            for (snapshot in dataSnapshot.children) {
                val song = snapshot.getValue(Song::class.java)
                song?.let { songs.add(it) }
            }
            SongQueue(songs)
        } catch (e: Exception) {
            // Handle the error
            null
        }
    }

    suspend fun getApprovedQueueAsList(roomCode: String): List<Song>? {
        val queueRef = database.child("$roomCode/approvedQueue")
        return try {
            val dataSnapshot = queueRef.get().await()
            val songs = mutableListOf<Song>()
            for (snapshot in dataSnapshot.children) {
                val song = snapshot.getValue(Song::class.java)
                song?.let { songs.add(it) }
            }
            songs
        } catch (e: Exception) {
            // Handle the error
            null
        }
    }

    suspend fun getPreviousQueue(roomCode: String): List<Song>? {
        val queueRef = database.child("$roomCode/previousQueue")
        return try {
            val dataSnapshot = queueRef.get().await()
            val songs = mutableListOf<Song>()
            for (snapshot in dataSnapshot.children) {
                val song = snapshot.getValue(Song::class.java)
                song?.let { songs.add(it) }
            }
            songs
        } catch (e: Exception) {
            // Handle the error
            null
        }
    }


    fun getDeniedQueue(roomCode: String, callback: (SongQueue) -> Unit) {
        val queueRef = database.child("$roomCode/deniedQueue")

        queueRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val songs = mutableListOf<Song>()
                for (snapshot in dataSnapshot.children) {
                    val song = snapshot.getValue(Song::class.java)
                    song?.let { songs.add(it) }
                }
                val songQueue = SongQueue(songs)
                callback(songQueue)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(SongQueue()) // Invoke the callback with an empty SongQueue to indicate an error or cancellation
            }
        })
    }

    fun getUsers(roomCode: String, callback: (List<User>) -> Unit) {
        val userRef = database.child("$roomCode/users")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }
                callback(userList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(emptyList()) // Invoke the callback with an empty list to indicate an error or cancellation
            }
        })
    }

    suspend fun getCurrentSong(roomCode: String): Song? {
        val queueRef = database.child("$roomCode/approvedQueue")
        return try {
            val dataSnapshot = queueRef.get().await()
            val songs = mutableListOf<Song>()
            for (snapshot in dataSnapshot.children) {
                val song = snapshot.getValue(Song::class.java)
                song?.let { songs.add(it) }
            }
            songs.filter { it.hostOrder == 0 }
            songs.firstOrNull()
        } catch (e: Exception) {
            // Handle the error
            null
        }
    }
    suspend fun getNextSong(roomCode: String): Song? {
        val queueRef = database.child("$roomCode/approvedQueue")
        return try {
            val dataSnapshot = queueRef.get().await()
            val songs = mutableListOf<Song>()
            for (snapshot in dataSnapshot.children) {
                val song = snapshot.getValue(Song::class.java)
                song?.let { songs.add(it) }
            }
            songs.firstOrNull{ it.hostOrder == 1}
        } catch (e: Exception) {
            // Handle the error
            null
        }
    }
    suspend fun getPrevSong(roomCode: String): Song? {
        val queueRef = database.child("$roomCode/previousQueue")
        return try {
            val dataSnapshot = queueRef.get().await()
            val songs = mutableListOf<Song>()
            for (snapshot in dataSnapshot.children) {
                val song = snapshot.getValue(Song::class.java)
                song?.let { songs.add(it) }
            }
            songs.firstOrNull{ it.hostOrder == 0 }
        } catch (e: Exception) {
            // Handle the error
            null
        }
    }
    suspend fun advanceSong(roomCode: String) {
        val queueRef = database.child("$roomCode/approvedQueue")
        try {
            val dataSnapshot = queueRef.get().await()
            val songs = mutableListOf<Song>()
            for (snapshot in dataSnapshot.children) {
                val song = snapshot.getValue(Song::class.java)
                song?.let { songs.add(it) }
            }
            if (songs.size > 1) {
                val nextSong = songs.firstOrNull{ it.hostOrder == 1 }
                if (nextSong != null) {
                    setNewDuration(roomCode, nextSong.duration)
                    setNewSong(roomCode, nextSong.context_uri)
                }
                val firstSong = songs.firstOrNull{ it.hostOrder == 0 }
                removeSongFromApprovedQueue(roomCode, firstSong)


                for (song in songs) {
                    if (song.hostOrder != 0) {
                        setSongHostOrder(roomCode, song, song.hostOrder - 1)
                    }
                }
                addSongToPreviousQueue(roomCode, firstSong)
            }
        } catch (e: Exception) {

        }
    }
    suspend fun moveBackSong(roomCode: String) {
        val queueRef = database.child("$roomCode/previousQueue")
        try {
            val dataSnapshot = queueRef.get().await()
            val songs = mutableListOf<Song>()
            for (snapshot in dataSnapshot.children) {
                val song = snapshot.getValue(Song::class.java)
                song?.let { songs.add(it) }
            }
            if (songs.isNotEmpty()) {
                val lastSong = songs.firstOrNull{ it.hostOrder == 0 }
                removeSongFromPreviousQueue(roomCode, lastSong)
                if (lastSong != null) {
                    setNewDuration(roomCode, lastSong.duration)
                    setNewSong(roomCode, lastSong.context_uri)
                }

                for (song in songs) {
                    if (song.hostOrder != 0) {
                        setPreviousSongHostOrder(roomCode, song, song.hostOrder - 1)
                    }
                }
                addSongToApprovedQueueHead(roomCode, lastSong)
            }
        } catch (e: Exception) {

        }
    }

    fun setNewDuration(roomCode: String, duration: Int) {
        val currentSongDurationRef = database.child("$roomCode/currentSongDuration")
        currentSongDurationRef.setValue(duration)
    }

    fun setNewSong(roomCode: String, contextUri: String) {
        val currentSongRef = database.child("$roomCode/currentSong")
        currentSongRef.setValue(contextUri)
    }

    fun getSongDurationCallback(roomCode: String, callback: (Int) -> Unit) {
        val queueRef = database.child("$roomCode/currentSongDuration")

        queueRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var duration = 0
                for (snapshot in dataSnapshot.children) {
                    val d = snapshot.getValue(Int::class.java)
                    if (d!= null) duration = d
                }

                callback(duration)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback(0) // Invoke the callback with an empty SongQueue to indicate an error or cancellation
            }
        })
    }

    fun getCurrentSongCallback(roomCode: String, callback: (String) -> Unit) {
        val queueRef = database.child("$roomCode/currentSong")

        queueRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var song = ""
                for (snapshot in dataSnapshot.children) {
                    val s = snapshot.getValue(String::class.java)
                    if (s != null) song = s
                }

                callback(song)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                callback("") // Invoke the callback with an empty SongQueue to indicate an error or cancellation
            }
        })
    }
}