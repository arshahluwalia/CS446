package com.example.jukebox.util

import android.app.Activity

import android.view.inputmethod.InputMethodManager

class HideSoftKeyboard {

	companion object {
		fun hideSoftKeyboard(activity: Activity) {
			val inputMethodManager = activity.getSystemService(
				Activity.INPUT_METHOD_SERVICE
			) as InputMethodManager
			if (inputMethodManager.isAcceptingText) {
				inputMethodManager.hideSoftInputFromWindow(
					activity.currentFocus!!.windowToken,
					0
				)
			}
		}
	}
}