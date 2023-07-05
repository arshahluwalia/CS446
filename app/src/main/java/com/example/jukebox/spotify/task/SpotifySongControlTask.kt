package com.example.jukebox.spotify.task

import android.util.Log
import com.example.jukebox.spotify.RetrofitHelper
import com.example.jukebox.spotify.SpotifyApi
import com.example.jukebox.spotify.models.SpotifyPlayBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

object SpotifySongControlTask : CoroutineScope by MainScope()  {

    suspend fun playPreviousSong(userTokensList : MutableList<String>) {
        val api = RetrofitHelper.getAPIUrlInstance().create(SpotifyApi::class.java)
        for (accessToken in userTokensList){
            Log.d("spotify control task: Token", accessToken)
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

    suspend fun playSong(context_uri: String, position: Int, userTokensList: MutableList<String>) {
        val api = RetrofitHelper.getAPIUrlInstance().create(SpotifyApi::class.java)
        val spotifyPlayBody = SpotifyPlayBody(context_uri, position)

        for (accessToken in userTokensList){
            Log.d("spotify control task: Token", accessToken)
            val job = async {
                val executeJob: suspend (accessToken: String) -> Unit = { token ->
                    val result = api.playSong(
                        "Bearer $accessToken",
                        "application/json",
                        spotifyPlayBody
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

    suspend fun pauseSong(userTokensList : MutableList<String>) {
        val api = RetrofitHelper.getAPIUrlInstance().create(SpotifyApi::class.java)
        for (accessToken in userTokensList){
            Log.d("spotify control task: Token", accessToken)
            val job = async {
                val executeJob: suspend (accessToken: String) -> Unit = { token ->
                    val result = api.pauseSong(
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

    suspend fun getPlaybackState(token: String): Pair<String?, Long?> {
        val api = RetrofitHelper.getAPIUrlInstance().create(SpotifyApi::class.java)
        var fetchedContextUri = ""
        var fetchedOffset : Long = 0
        val job = async {
            val executeJob: suspend (accessToken: String) -> Unit = { token ->
                val result = api.getPlaybackState("Bearer $token", "application/json")

                if (result.body() != null) {
                    Log.d("spotify fetch state: ", result.body().toString())
                    val playbackState = result.body()

                    val contextUri = playbackState?.context?.uri
                    val offset = playbackState?.progress_ms
                    if (contextUri != null) {
                        fetchedContextUri = contextUri
                    }
                    if (offset != null) {
                        fetchedOffset = offset
                    }
                } else {
                    Log.d("spotify fetch state: ", "null response")
                }
            }
            executeJob(token)
        }
        job.await()
        return Pair(fetchedContextUri, fetchedOffset)
    }
}