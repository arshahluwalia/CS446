package com.example.jukebox

import android.media.Image
import android.os.Bundle
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDirection.Companion.Content
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jukebox.ui.theme.Black
import com.example.jukebox.ui.theme.PurpleNeon
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle

class WelcomeActivity  : ComponentActivity(){
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Background()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Background() {
    var roomCode by remember { mutableStateOf("") }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            //.scale(maxOf(aspectRatio, 1f), maxOf(1 / aspectRatio, 1f))
    ) {
        val aspectRatio = maxWidth/maxHeight
        Box(
            Modifier
                .fillMaxSize()
                .scale(maxOf(aspectRatio, 1f), maxOf(1 / aspectRatio, 1f))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PurpleNeon,
                            Black
                        ),
                    )
                )
        )

        Column(
            modifier = Modifier
                .padding(top = maxHeight / 4)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            ) {
            Text(
                text = "JukeBox",
                fontSize = 60.sp,
                color = Color.White
            )
            Text(
                text = "Stop looking for the party aux",
                fontSize = 20.sp,
                color = Color.White
            )
        }

        Row(
            modifier = Modifier
                .padding(bottom = maxHeight / 6)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = roomCode,
                onValueChange = {
                    roomCode = it
                },
                label = { Text("Enter your room code") },
                maxLines = 2,
                //            textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
                shape = RoundedCornerShape(20)
                // TODO: need to add QR code (trailing icon), need to handle input, need round stroke cap
            )

            Box(
                modifier = Modifier
                    .clickable { /* Handle box click action */ }
                    .size(50.dp)
            ) {
                Image(
                    painterResource(id = R.drawable.qr_icon),
                    contentDescription = "QR Icon",
                    modifier = Modifier.fillMaxSize().background(color = Color.White)
                )
            }
        }

        Button(
            modifier = Modifier
                .padding(bottom = maxHeight / 12)
                .align(Alignment.BottomCenter),
            elevation =  ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp,
                pressedElevation = 15.dp,
                disabledElevation = 0.dp
            ),
            shape = RoundedCornerShape(20),
            onClick = {}
        ) {
            Text(text = AnnotatedString("Start a Room"))
        }
    }
}
