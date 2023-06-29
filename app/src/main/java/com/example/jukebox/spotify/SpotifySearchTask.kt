package com.example.jukebox.spotify

import android.media.Image
import android.util.Log
import com.example.jukebox.Song
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlinx.coroutines.async

object SpotifySearchTask : CoroutineScope by MainScope() {

        suspend fun requestTrackID(songName: String) : List<Song> {
            var listOfSongs = mutableListOf<Song>()
            if (!SpotifyAccessToken.isTokenValid()) {
                val api = RetrofitHelper.getAPIUrlInstance().create(SpotifyApi::class.java)
                val accessToken = "Bearer BQB54CEzp_Z_QQaR9GKqd1uCRoN8PNKfMlkgjda4VtUUxDONX1x4tUyE0G7lzyUfN_0COAJfu1CqCEpAW9Khp2_NDaFcQX8xPO5Uy8qyHREeJI7LtyuEum3KE5dFXf6Kq4o127J4ODe-t4OszAVyIVOCkbn0ksPTIbvMdQiuCIVssc21SANDRGDGD4-cnhcHJX15p0ezhlrFSd5gyst9ZcScRynNonhlF6LstSSJmJ_yyHZwY5w"

                val job = async {
                    val result = api.searchSong(
                        accessToken,
                        "application/json",
                        songName,
                    )

                    if (result.body() != null) {
                        Log.d("spotify logging: ", "received response ${result.body()!!.tracks}")
                        for (item in result.body()!!.tracks.items) {
                            var songArtist = item.artists[0].name
                            var songTitle = item.name
                            var songUri = item.uri
                            var newSong = Song(songUri, songTitle, songArtist)
                            listOfSongs.add(newSong)
                            Log.d("spotify logging: ", "added new song: artist: ${newSong.songArtist}, title: ${newSong.songTitle}, uri: ${newSong.context_uri}")
                        }
                    } else {
                        Log.d("spotify logging: ", "null response")
                    }
                }
                job.await()
            }
            Log.d("spotify logging: ", "reached here")
            for (newSong in listOfSongs) {
                Log.d("spotify logging: ", "after added new song: artist: ${newSong.songArtist}, title: ${newSong.songTitle}, uri: ${newSong.context_uri}")

            }
            return listOfSongs
        }
        private fun parseTrackIdFromJson(jsonResponse: JSONObject) {
            Log.d("ADD", jsonResponse.toString())
        }


    }

data class SpotifySearchResponse(
    val tracks: SpotifyTracks
)

data class SpotifyTracks(
    val href: String,
    val items: List<SpotifyTrack>,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int
)

data class SpotifyTrack(
    val album: SpotifyAlbum,
    val artists: List<SpotifyArtist>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val external_ids: SpotifyExternalIds,
    val external_urls: SpotifyExternalUrls,
    val href: String,
    val id: String,
    val is_local: Boolean,
    val is_playable: Boolean,
    val name: String,
    val popularity: Int,
    val preview_url: String?,
    val track_number: Int,
    val type: String,
    val uri: String
)

data class SpotifyAlbum(
    val album_type: String,
    val artists: List<SpotifyArtist>,
    val external_urls: SpotifyExternalUrls,
    val href: String,
    val id: String,
    val images: List<SpotifyImage>,
    val is_playable: Boolean,
    val name: String,
    val release_date: String,
    val release_date_precision: String,
    val total_tracks: Int,
    val type: String,
    val uri: String
)

data class SpotifyArtist(
    val external_urls: SpotifyExternalUrls,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)

data class SpotifyExternalIds(
    val isrc: String
)

data class SpotifyExternalUrls(
    val spotify: String
)

data class SpotifyImage(
    val height: Int,
    val url: String,
    val width: Int
)

