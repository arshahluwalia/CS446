package com.example.jukebox

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jukebox.ui.theme.Black
import com.example.jukebox.ui.theme.PurpleNeon

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                ScreenContent()
            }
        }
    }
}

@Composable
fun ScreenContent() {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        WelcomeScreenBackground()
        JukeBoxTitle()
        RoomCodeTextField()
        StartARoomButton()
    }
}

@Composable
fun BoxWithConstraintsScope.WelcomeScreenBackground() {
    val aspectRatio = maxWidth / maxHeight
    Box(
        Modifier
            .fillMaxSize()
            .scale(maxOf(aspectRatio, 1f), maxOf(1 / aspectRatio, 1f))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PurpleNeon,
                        Black
                    )
                )
            )
    )
}

@Composable
fun BoxWithConstraintsScope.JukeBoxTitle() {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxWithConstraintsScope.RoomCodeTextField() {
    var roomCode by remember { mutableStateOf("") }
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
                roomCode = it // TODO: need to handle input
            },
            label = { Text("Enter your room code") },
            maxLines = 2,
            //            textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
            shape = RoundedCornerShape(20)
        )

        Box(
            modifier = Modifier
                .clickable { /* Handle box click action */ }
                .size(50.dp)
        ) {
            Image(
                painterResource(id = R.drawable.qr_icon),
                contentDescription = "QR Icon",
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
            )
        }
    }
}

@Composable
fun BoxWithConstraintsScope.StartARoomButton() {
    val context = LocalContext.current
    Button(
        modifier = Modifier
            .padding(bottom = maxHeight / 12)
            .align(Alignment.BottomCenter),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        shape = RoundedCornerShape(20),
        onClick = {
            val intent = Intent(context, SongQueueActivity::class.java)
            context.startActivity(intent)
        }
    ) {
        Text(text = AnnotatedString("Start a Room"))
    }
}
