package com.example.jukebox.spotify

import com.example.jukebox.spotify.models.AccessToken
import com.example.jukebox.spotify.models.RequestStatus
import com.example.jukebox.spotify.models.SpotifySearchResponse
import com.example.jukebox.spotify.models.SpotifyPlaybackState
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import com.example.jukebox.spotify.models.SpotifyPlayBody


interface SpotifyApi {
	@POST("api/token")
	@FormUrlEncoded
	suspend fun getAccessToken(
		@Header("Authorization") auth: String,
		@Header("Content-Type") content: String,
		@Field("grant_type") grantType: String
	) : Response<AccessToken>

	@GET("search?type=track&market=CA&limit=5")
	suspend fun searchSong(
		@Header("Authorization") auth: String,
		@Header("Content-Type") content: String,
		@Query("q") songName: String,
	) : Response<SpotifySearchResponse>

	@PUT("me/player/play")
	suspend fun playSong(
		@Header("Authorization") auth: String,
		@Header("Content-Type") content: String,
		@Body spotifyPlayBody: SpotifyPlayBody
	): Response<RequestStatus>

	@PUT("me/player/pause")
	suspend fun pauseSong(
		@Header("Authorization") auth: String,
		@Header("Content-Type") content: String
	): Response<RequestStatus>

	@PUT("me/player/play")
	suspend fun resumeSong(
		@Header("Authorization") auth: String,
		@Header("Content-Type") content: String
	): Response<RequestStatus>

	@GET("me/player")
	suspend fun getPlaybackState(
		@Header("Authorization") auth: String,
		@Header("Content-Type") content: String
	) : Response<SpotifyPlaybackState>
}

