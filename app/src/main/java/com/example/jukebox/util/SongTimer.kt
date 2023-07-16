package com.example.jukebox.util

import android.os.CountDownTimer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow


class SongTimer constructor(
	private val duration: Long,
	private val currentTime: MutableStateFlow<Int>
) : CountDownTimer(duration, INTERVAL_MS) {

	override fun onTick(msUntilFinished: Long) {
		val second = (msUntilFinished / 1000).toInt()
		Log.d("song time ontick: ", (msUntilFinished / 1000).toString())
		currentTime.value = second
	}

	override fun onFinish() {
		onTick(duration / 1000)
		currentTime.value = 0
	}

	companion object {
		private const val INTERVAL_MS: Long = 1000
	}
}