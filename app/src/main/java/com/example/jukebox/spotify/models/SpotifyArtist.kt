package com.example.jukebox.spotify.models

data class SpotifyArtist(
	val external_urls: SpotifyExternalUrls,
	val href: String,
	val id: String,
	val name: String,
	val type: String,
	val uri: String
)