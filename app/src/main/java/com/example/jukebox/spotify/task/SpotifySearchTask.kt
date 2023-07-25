package com.example.jukebox.spotify.task

import android.util.Log
import com.example.jukebox.Song
import com.example.jukebox.spotify.RetrofitHelper
import com.example.jukebox.spotify.SpotifyAccessToken
import com.example.jukebox.spotify.SpotifyApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import org.json.JSONObject

object SpotifySearchTask : CoroutineScope by MainScope() {

    suspend fun requestTrackID(songName: String) : List<Song> {
        val listOfSongs = mutableListOf<Song>()
        if (SpotifyAccessToken.isTokenValid()) {
            val api = RetrofitHelper.getAPIUrlInstance().create(SpotifyApi::class.java)
            val accessToken = "Bearer ${SpotifyAccessToken.getToken()}"

            val job = async {
                val result = api.searchSong(
                    accessToken,
                    "application/json",
                    songName,
                )

                if (result.body() != null) {
                    Log.d("spotify logging: ", "received response ${result.body()!!.tracks}")
                    for (item in result.body()!!.tracks.items) {
                        val songArtist = item.artists[0].name
                        val songTitle = item.name
                        val songUri = item.uri
                        val duration = item.duration_ms
                        val timeStamp = System.currentTimeMillis()
                        val newSong = Song(songUri, songTitle, songArtist, duration= duration, timeStampAdded = timeStamp, hostOrder = -1)
                        listOfSongs.add(newSong)
                        Log.d("spotify logging: ", "added new song: artist: ${newSong.songArtist}, title: ${newSong.songTitle}, uri: ${newSong.context_uri}")
                    }
                } else {
                    Log.d("spotify logging: ", "null response")
                }
            }
            job.await()
        } else {
            Log.d("spotify logging: ", "token is not valid")
        }

        for (newSong in listOfSongs) {
            Log.d("spotify logging: ", "after added new song: artist: ${newSong.songArtist}, title: ${newSong.songTitle}, uri: ${newSong.context_uri}")

        }
        return listOfSongs
    }
    private fun parseTrackIdFromJson(jsonResponse: JSONObject) {
        Log.d("ADD", jsonResponse.toString())
    }
}
