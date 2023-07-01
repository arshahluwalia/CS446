package com.example.jukebox.spotify.models

data class AccessToken (
	val access_token: String,
	val token_type: String,
	val expires_in: Int
)
