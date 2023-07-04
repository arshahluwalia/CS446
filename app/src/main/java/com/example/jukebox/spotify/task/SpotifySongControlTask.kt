package com.example.jukebox.spotify.task

import android.util.Log
import com.example.jukebox.Song
import com.example.jukebox.spotify.RetrofitHelper
import com.example.jukebox.spotify.SpotifyAccessToken
import com.example.jukebox.spotify.SpotifyApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow

object SpotifySongControlTask : CoroutineScope by MainScope()  {

    suspend fun playPreviousSong(userTokensList : MutableList<String>) {
        val api = RetrofitHelper.getAPIUrlInstance().create(SpotifyApi::class.java)
        for (accessToken in userTokensList){
//            Log.d("spotify control task: Token", accessToken)
            val job = async {
                val executeJob: suspend (accessToken: String) -> Unit = { token ->
                    val result = api.skipToPrevious(
                        "Bearer $accessToken",
                        "application/json",
                    )

                    if (result.body() != null) {
                        Log.d("spotify control: ", result.body().toString())
                    } else {
                        Log.d("spotify control: ", "null response")
                    }
                }
                executeJob(accessToken)
            }
            job.await()
        }
    }
}