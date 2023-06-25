package com.example.jukebox.spotify

object SpotifyUserToken {
	// TODO: Deprecate this and pull from the database so we dont have 2 sources of truth
	private var token = ""

	fun getToken() = token

	fun setToken(newToken: String) { token = newToken }
}