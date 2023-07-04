package com.example.jukebox.spotify.models

data class SpotifyPlaybackState(
    val device: Device?,
    val repeat_state: String,
    val shuffle_state: Boolean,
    val context: Context?,
    val timestamp: Long,
    val progress_ms: Long,
    val is_playing: Boolean,
    val item: Track?,
    val currently_playing_type: String,
    val actions: Actions?
)

data class Device(
    val id: String,
    val is_active: Boolean,
    val is_private_session: Boolean,
    val is_restricted: Boolean,
    val name: String,
    val type: String,
    val volume_percent: Int
)

data class Context(
    val type: String,
    val href: String,
    val external_urls: ExternalUrls,
    val uri: String
)

data class ExternalUrls(
    val spotify: String
)

data class Track(
    val album: Album,
    val artists: List<Artist>,
    val available_markets: List<String>,
    val disc_number: Int,
    val duration_ms: Long,
    val explicit: Boolean,
    val external_ids: ExternalIds,
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val is_playable: Boolean,
    val restrictions: Restrictions?,
    val name: String,
    val popularity: Int,
    val preview_url: String?,
    val track_number: Int,
    val type: String,
    val uri: String,
    val is_local: Boolean
)

data class Album(
    val album_type: String,
    val total_tracks: Int,
    val available_markets: List<String>,
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val release_date: String,
    val release_date_precision: String,
    val restrictions: Restrictions?,
    val type: String,
    val uri: String,
    val copyrights: List<Copyright>,
    val external_ids: ExternalIds,
    val genres: List<String>,
    val label: String,
    val popularity: Int,
    val album_group: String,
    val artists: List<Artist>
)

data class Artist(
    val external_urls: ExternalUrls,
    val followers: Followers,
    val genres: List<String>,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val popularity: Int,
    val type: String,
    val uri: String
)

data class Image(
    val url: String,
    val height: Int,
    val width: Int
)

data class ExternalIds(
    val isrc: String?,
    val ean: String?,
    val upc: String?
)

data class Restrictions(
    val reason: String
)

data class Copyright(
    val text: String,
    val type: String
)

data class Actions(
    val interrupting_playback: Boolean,
    val pausing: Boolean,
    val resuming: Boolean,
    val seeking: Boolean,
    val skipping_next: Boolean,
    val skipping_prev: Boolean,
    val toggling_repeat_context: Boolean,
    val toggling_shuffle: Boolean,
    val toggling_repeat_track: Boolean,
    val transferring_playback: Boolean
)

data class Followers(
    val href: String?,
    val total: Int
)
