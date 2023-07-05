package com.example.jukebox.spotify.models

data class SpotifyPlayBody(
    val uris: List<String>,
    val position_ms: Int
)