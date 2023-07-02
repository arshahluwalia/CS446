package com.example.jukebox.spotify.task

import android.util.Log
import com.example.jukebox.Song
import com.example.jukebox.spotify.RetrofitHelper
import com.example.jukebox.spotify.SpotifyAccessToken
import com.example.jukebox.spotify.SpotifyApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

object SpotifySongControlTask : CoroutineScope by MainScope()  {

    suspend fun playPreviousSong(userTokensList : List<String>) {
        if (SpotifyAccessToken.isTokenValid()) {
            val api = RetrofitHelper.getAPIUrlInstance().create(SpotifyApi::class.java)

            for (accessToken in userTokensList){
                val job = async {
                    val executeJob: suspend (accessToken: String) -> Unit = { token ->
                        val result = api.skipToPrevious(
                            token,
                            "application/json",
                        )

                        if (result.body() != null) {
                            Log.d("spotify logging: ", result.body().toString())
                        } else {
                            Log.d("spotify logging: ", "null response")
                        }
                    }
                    executeJob(accessToken)
                }
                job.await()
            }
        } else {
            Log.d("spotify logging: ", "token is not valid")
        }

    }
}