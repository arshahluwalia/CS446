package com.example.jukebox.spotify

import android.media.Image
import android.util.Log
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URLEncoder

object SpotifySearchTask : CoroutineScope by MainScope() {

        fun requestTrackID(songName: String) {
            if (!SpotifyAccessToken.isTokenValid()) {
                val api = RetrofitHelper.getAPIUrlInstance().create(SpotifyApi::class.java)
                val accessToken = "Bearer ${SpotifyUserToken.getToken()}"

                launch {
                    val result = api.searchSong(
                        accessToken,
                        "application/json",
                        songName,
                    )

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

class Track {
    var artists: List<ArtistSimple>? = null

    @SerializedName("available_markets")
    var availableMarkets: List<String>? = null

    @SerializedName("disc_number")
    var discNumber = 0

    @SerializedName("duration_ms")
    var durationMillis: Long = 0
    var explicit = false

    @SerializedName("external_urls")
    var externalUrls: Map<String, String>? = null
    var href: String? = null
    var id: String? = null
    var name: String? = null

    @SerializedName("preview_url")
    var previewUrl: String? = null

    @SerializedName("track_number")
    var trackNumber = 0
    var type: String? = null
    var uri: String? = null
    var album: AlbumSimple? = null

    @SerializedName("external_ids")
    var externalIds: Map<String, String>? = null
    var popularity = 0
}

class ArtistSimple {
    @SerializedName("external_urls")
    var externalUrls: Map<String, String>? = null
    var href: String? = null
    var id: String? = null
    var name: String? = null
    var type: String? = null
    var uri: String? = null
}

class AlbumSimple {
    var id: String? = null
    var href: String? = null
    var name: String? = null
    var uri: String? = null
    var type: String? = null
    var images: List<Image>? = null

    @SerializedName("album_type")
    var albumType: String? = null

    @SerializedName("available_markets")
    var availableMarkets: List<String>? = null

    @SerializedName("external_urls")
    var externalUrls: Map<String, String>? = null
}
