package com.example.jukebox.spotify.task

import android.util.Base64
import android.util.Log
import com.example.jukebox.BuildConfig
import com.example.jukebox.spotify.RetrofitHelper
import com.example.jukebox.spotify.SpotifyAccessToken
import com.example.jukebox.spotify.SpotifyApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

private const val clientID = BuildConfig.SPOTIFY_CLIENT_ID
private const val clientSECRET = BuildConfig.SPOTIFY_CLIENT_SECRET

object SpotifyAccessTokenTask : CoroutineScope by MainScope() {

	fun requestAccessToken() {
		if (!SpotifyAccessToken.isTokenValid()) {
			val api = RetrofitHelper.getAccountUrlInstance().create(SpotifyApi::class.java)
			val base64String = "Basic " + get64BaseString("$clientID:$clientSECRET")

			launch {
				val result = api.getAccessToken(
					base64String,
					"application/x-www-form-urlencoded",
					"client_credentials"
				)

				if (result.body() != null) {
					Log.d("spotify logging: ", result.body().toString())
					val token = result.body()
					SpotifyAccessToken.setToken(
						newToken = token!!.access_token,
						newExpiryTimestamp = Clock.System.now() + token!!.expires_in.seconds
					)
				} else {
					Log.d("spotify logging: ", "null response")
				}
			}
		}
	}

	private fun get64BaseString(value: String) : String {
		return Base64.encodeToString(value.toByteArray(), Base64.NO_WRAP)
	}
}