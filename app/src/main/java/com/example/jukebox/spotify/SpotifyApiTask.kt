package com.example.jukebox.spotify

import android.media.Image
import android.util.Log
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URLEncoder


object SpotifyApiTask : CoroutineScope by MainScope() {

        fun requestTrackID(songName: String) {
            if (!SpotifyAccessToken.isTokenValid()) {
                val api = RetrofitHelper.getAPIUrlInstance().create(SpotifyApi::class.java)
                var encodedSongName = URLEncoder.encode(songName, "UTF-8")
                val accessToken = SpotifyUserToken.getToken()
                launch {
                    val result = api.searchSong(
                        accessToken,
                        "application/json",
                        encodedSongName,
                    )
//                    Log.d("spotify logging: ", result.body()
                    if (result.body() != null) {
                        Log.d("spotify logging: ", result.body().toString())
                    } else {
                        Log.d("spotify logging: ", "null response")
                    }
                }
            }
        }
        private fun parseTrackIdFromJson(jsonResponse: JSONObject) {
            Log.d("ADD", jsonResponse.toString())
        }


    }

