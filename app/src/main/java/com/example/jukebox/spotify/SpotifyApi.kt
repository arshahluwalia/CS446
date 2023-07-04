package com.example.jukebox.spotify

import com.example.jukebox.spotify.models.AccessToken
import com.example.jukebox.spotify.models.RequestStatus
import com.example.jukebox.spotify.models.SpotifySearchResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


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

	@POST("me/player/previous")
	suspend fun skipToPrevious(
		@Header("Authorization") auth: String,
		@Header("Content-Type") content: String
	): Response<RequestStatus>

}

