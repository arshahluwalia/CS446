package com.example.jukebox.util

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

private lateinit var songUri : String

class OpenSpotifySongActivity : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songUri = intent.getStringExtra("songUri").toString()

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(songUri))
        intent.setPackage("com.spotify.music")
        try {
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this,"Please install Spotify App first.",Toast.LENGTH_SHORT).show()
        }

    }
}
