package com.example.jukebox.spotify

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

	private const val accountUrl = "https://accounts.spotify.com"
	private const val spotifyApiUrl = "https://api.spotify.com/v1/"

	fun getAccountUrlInstance(): Retrofit {
		return Retrofit.Builder().baseUrl(accountUrl)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}

	fun getAPIUrlInstance(): Retrofit {
		return Retrofit.Builder().baseUrl(spotifyApiUrl)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}
}