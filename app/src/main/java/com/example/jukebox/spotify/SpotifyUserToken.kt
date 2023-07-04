package com.example.jukebox.spotify

object SpotifyUserToken {
	// We need this since theres no way to get the user token just based on the room code
	private var token = ""

	fun getToken() = token

	fun setToken(newToken: String) { token = newToken }
}