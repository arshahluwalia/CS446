package com.example.jukebox.spotify.task

import android.util.Log
import com.example.jukebox.Song
import com.example.jukebox.spotify.RetrofitHelper
import com.example.jukebox.spotify.SpotifyAccessToken
import com.example.jukebox.spotify.SpotifyApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

class SpotifySongControlTask : CoroutineScope by MainScope()  {

    suspend fun playPreviousSong(roomCode: String) {
        if (SpotifyAccessToken.isTokenValid()) {
            val api = RetrofitHelper.getAPIUrlInstance().create(SpotifyApi::class.java)
            val accessToken = "Bearer ${SpotifyAccessToken.getToken()}"
            //TODO: Perform job for each deviceID
            val job = async {
                val result = api.skipToPrevious(
                    "", //deviceID
                    accessToken,
                    "application/json",
                )

                if (result.body() != null) {
                    Log.d("spotify logging: ", result.body().toString())
                } else {
                    Log.d("spotify logging: ", "null response")
                }
            }
            job.await()
        } else {
            Log.d("spotify logging: ", "token is not valid")
        }

    }
}