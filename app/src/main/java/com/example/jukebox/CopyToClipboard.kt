package com.example.jukebox

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class CopyToClipboard {

	companion object {
		fun copyToClipboard(context: Context, label: String, text: String) {
			val clipboardManager =
				context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
			val clip = ClipData.newPlainText(label, text)
			clipboardManager.setPrimaryClip(clip)
		}
	}
}