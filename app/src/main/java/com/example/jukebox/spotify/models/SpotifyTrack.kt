package com.example.jukebox.spotify.models

data class SpotifyTrack(
	val album: SpotifyAlbum,
	val artists: List<SpotifyArtist>,
	val disc_number: Int,
	val duration_ms: Int,
	val explicit: Boolean,
	val external_ids: SpotifyExternalIds,
	val external_urls: SpotifyExternalUrls,
	val href: String,
	val id: String,
	val is_local: Boolean,
	val is_playable: Boolean,
	val name: String,
	val popularity: Int,
	val preview_url: String?,
	val track_number: Int,
	val type: String,
	val uri: String
)
