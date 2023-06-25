package com.example.jukebox.spotify

object SpotifyUserToken {

	private var token = ""

	fun getToken() = token

	fun setToken(newToken: String) { token = newToken }
}