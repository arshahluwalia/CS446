package com.example.jukebox.spotify

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface SpotifyApi {
	@POST("api/token")
	@FormUrlEncoded
	suspend fun getAccessToken(
		@Header("Authorization") auth: String,
		@Header("Content-Type") content: String,
		@Field("grant_type") grantType: String
	) : Response<AccessToken>
}