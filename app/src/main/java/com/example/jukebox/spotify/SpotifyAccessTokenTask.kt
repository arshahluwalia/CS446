package com.example.jukebox.spotify

import android.util.Base64
import android.util.Log
import com.example.jukebox.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

private const val clientID = BuildConfig.SPOTIFY_CLIENT_ID
private const val clientSECRET = BuildConfig.SPOTIFY_CLIENT_SECRET

object SpotifyAccessTokenTask : CoroutineScope by MainScope() {

	@OptIn(ExperimentalTime::class)
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
					SpotifyAccessToken.setToken(newToken = token!!.access_token, newExpiryTimestamp = Clock.System.now() + token!!.expires_in.seconds)
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

data class AccessToken (
	val access_token: String,
	val token_type: String,
	val expires_in: Int
)
