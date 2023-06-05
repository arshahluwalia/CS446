package com.example.jukebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextDirection.Companion.Content
import com.example.jukebox.ui.theme.Black
import com.example.jukebox.ui.theme.PurpleNeon

class WelcomeActivity  : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Test("test text")
        }
    }
}

@Composable
fun Test(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PurpleNeon,
                        Black
                    )
                )
            )
    ) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

}