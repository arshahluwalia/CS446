package com.example.jukebox

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.util.HideSoftKeyboard
import kotlinx.coroutines.flow.MutableStateFlow

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomCode = intent.getStringExtra("roomCode").toString()
        val dispatcher = onBackPressedDispatcher
        val roomManager = RoomManager()
        val hostName = MutableStateFlow("")
        getHostName(roomCode, hostName)
        setContent {
            JukeboxTheme() {
                ScreenContent(
                    dispatcher = dispatcher,
                    roomCode = roomCode,
                    roomManager = roomManager,
                    hostName = hostName,
                    activity = this
                )
            }
        }
    }

    private fun getHostName(roomCode: String, hostName: MutableStateFlow<String>) {
        val roomManager = RoomManager()
        roomManager.getHostName(roomCode) { name ->
            hostName.value = name
        }
    }
}

@Composable
private fun ScreenContent(
    dispatcher: OnBackPressedDispatcher? = null,
    roomCode: String,
    roomManager: RoomManager?,
    hostName: MutableStateFlow<String>,
    activity: Activity?
) {
    Box {
        SecondaryBackground()
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                BackToQueueButton(dispatcher)
            }
            SettingsTitle()
            ChangeNameField(
                roomCode = roomCode,
                roomManager = roomManager,
                currentHostName = hostName,
                activity = activity
            )
        }
    }
}

@Composable
private fun BackToQueueButton(dispatcher: OnBackPressedDispatcher? = null) {
    TextButton(
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        shape = RoundedCornerShape(20),
        onClick = {
            dispatcher?.onBackPressed()
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier.padding(end = 10.dp),
                painter = painterResource(
                    id = R.drawable.arrow_back
                ),
                contentDescription = null
            )
            Text(
                text = "Back to Queue",
                color = Color.White,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
private fun SettingsTitle() {
    Text(
        text = "Session Settings",
        style = MaterialTheme.typography.titleSmall,
        color = Color.White,
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangeNameField(
    roomCode: String,
    roomManager: RoomManager?,
    currentHostName: MutableStateFlow<String>,
    activity: Activity?
) {
    var hostName by remember { mutableStateOf("") }

    Text(
        modifier = Modifier.padding(vertical = 20.dp),
        text = "Change name:",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White,
        textAlign = TextAlign.Center
    )
    TextField(
        modifier = Modifier.padding(vertical = 20.dp),
        value = hostName,
        onValueChange = {
            hostName = it
        },
        placeholder = {
            Text(
                text = currentHostName.collectAsState().value,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        shape = RoundedCornerShape(20),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (activity != null) {
                    HideSoftKeyboard.hideSoftKeyboard(activity = activity)
                }
                roomManager?.setHostName(roomCode, hostName)
            }
        )
    )
    Button(
        onClick = {
            if (activity != null) {
                HideSoftKeyboard.hideSoftKeyboard(activity = activity)
            }
            roomManager?.setHostName(roomCode, hostName)
        },
        enabled = hostName.isNotEmpty()
    ) {
        Text(text = "Save")
    }
}

@Composable
@Preview
private fun PreviewScreenContent() {
    JukeboxTheme() {
        ScreenContent(
            dispatcher = null,
            roomCode = "ABCDE",
            hostName = MutableStateFlow("Lucas"),
            roomManager = null,
            activity = null
        )
    }
}
