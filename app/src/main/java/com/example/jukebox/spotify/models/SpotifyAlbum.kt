package com.example.jukebox.spotify.models

data class SpotifyAlbum(
	val album_type: String,
	val artists: List<SpotifyArtist>,
	val external_urls: SpotifyExternalUrls,
	val href: String,
	val id: String,
	val images: List<SpotifyImage>,
	val is_playable: Boolean,
	val name: String,
	val release_date: String,
	val release_date_precision: String,
	val total_tracks: Int,
	val type: String,
	val uri: String
)
