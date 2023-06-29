package com.example.jukebox.spotify

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

object SpotifyAccessToken {
	private var token = ""
	private var expiryTimestamp: Instant? = null

	fun getToken() = token
	fun getExpiryTimestamp() = expiryTimestamp
	fun setToken(newToken: String, newExpiryTimestamp: Instant) {
		token = newToken
		expiryTimestamp = newExpiryTimestamp
	}
	fun isTokenValid(): Boolean =
		token != "" && expiryTimestamp != null && expiryTimestamp!! > Clock.System.now()
}