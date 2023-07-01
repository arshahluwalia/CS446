package com.example.jukebox.spotify.models

data class SpotifyTracks(
	val href: String,
	val items: List<SpotifyTrack>,
	val limit: Int,
	val next: String?,
	val offset: Int,
	val previous: String?,
	val total: Int
)
