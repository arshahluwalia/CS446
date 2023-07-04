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
import androidx.core.text.isDigitsOnly
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

        val maxUpvotes = MutableStateFlow(1)
        val maxSuggestions = MutableStateFlow(1)
        getMaxUpvotes(roomCode, maxUpvotes)
        getMaxSuggestions(roomCode, maxSuggestions)
        setContent {
            JukeboxTheme() {
                ScreenContent(
                    dispatcher = dispatcher,
                    roomCode = roomCode,
                    roomManager = roomManager,
                    hostName = hostName,
                    maxUpvotes = maxUpvotes,
                    maxSuggestions = maxSuggestions,
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

    private fun getMaxUpvotes(roomCode: String, maxUpvotes: MutableStateFlow<Int>) {
        val roomManager = RoomManager()
        roomManager.getMaxUpvotes(roomCode) { max ->
            maxUpvotes.value = max
        }
    }

    private fun getMaxSuggestions(roomCode: String, maxSuggestions: MutableStateFlow<Int>) {
        val roomManager = RoomManager()
        roomManager.getMaxSuggestions(roomCode) { max ->
            maxSuggestions.value = max
        }
    }
}

@Composable
private fun ScreenContent(
    dispatcher: OnBackPressedDispatcher? = null,
    roomCode: String,
    roomManager: RoomManager?,
    hostName: MutableStateFlow<String>,
    maxUpvotes: MutableStateFlow<Int>,
    maxSuggestions: MutableStateFlow<Int>,
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
            ChangeMaxSuggestions(
                roomCode = roomCode,
                roomManager = roomManager,
                maxSuggestions = maxSuggestions,
                activity = activity
            )
            ChangeMaxUpvotes(
                roomCode = roomCode,
                roomManager = roomManager,
                maxUpvotes = maxUpvotes,
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
        modifier = Modifier.padding(bottom = 20.dp),
        text = "Session Settings",
        style = MaterialTheme.typography.titleSmall,
        color = Color.White,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ChangeNameField(
    roomCode: String,
    roomManager: RoomManager?,
    currentHostName: MutableStateFlow<String>,
    activity: Activity?
) {
    var hostName by remember { mutableStateOf("") }

    Text(
        modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth().padding(start = 60.dp),
        text = "Change Name:",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White,
        textAlign = TextAlign.Start
    )
    TextField(
        modifier = Modifier.padding(vertical = 0.dp),
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
        colors = TextFieldDefaults.colors(
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
private fun ChangeMaxUpvotes(
    roomCode: String,
    roomManager: RoomManager?,
    maxUpvotes: MutableStateFlow<Int>,
    activity: Activity?
) {
    var MaxUpvotes by remember { mutableStateOf("") }

    Text(
        modifier = Modifier.padding(vertical = 40.dp).fillMaxWidth().padding(start = 60.dp),
        text = "Change Maximum Upvotes:",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White,
        textAlign = TextAlign.Start
    )
    TextField(
        modifier = Modifier.padding(vertical = 0.dp),
        value = MaxUpvotes,
        onValueChange = {
            MaxUpvotes = it
        },
        placeholder = {
            Text(
                text = maxUpvotes.collectAsState().value.toString(),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        shape = RoundedCornerShape(20),
        singleLine = true,
        colors = TextFieldDefaults.colors(
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
                if (MaxUpvotes.isDigitsOnly() && !MaxUpvotes.equals("")) {
                    roomManager?.setMaxUpvotes(roomCode, MaxUpvotes.toInt())
                }
            }
        )
    )
    Button(
        onClick = {
            if (activity != null) {
                HideSoftKeyboard.hideSoftKeyboard(activity = activity)
            }
            if (MaxUpvotes.isDigitsOnly() && !MaxUpvotes.equals("")) {
                roomManager?.setMaxUpvotes(roomCode, MaxUpvotes.toInt())
            }
        },
        enabled = MaxUpvotes.isNotEmpty()
    ) {
        Text(text = "Save")
    }
}

@Composable
private fun ChangeMaxSuggestions(
    roomCode: String,
    roomManager: RoomManager?,
    maxSuggestions: MutableStateFlow<Int>,
    activity: Activity?
) {
    var MaxSuggestions by remember { mutableStateOf("") }

    Text(
        modifier = Modifier.padding(vertical = 60.dp).fillMaxWidth().padding(start = 60.dp),
        text = "Change Maximum Suggestions:",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White,
        textAlign = TextAlign.Start
    )
    TextField(
        modifier = Modifier.padding(vertical = 0.dp),
        value = MaxSuggestions,
        onValueChange = {
            MaxSuggestions = it
        },
        placeholder = {
            Text(
                text = maxSuggestions.collectAsState().value.toString(),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        shape = RoundedCornerShape(20),
        singleLine = true,
        colors = TextFieldDefaults.colors(
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
                if (MaxSuggestions.isDigitsOnly() && !MaxSuggestions.equals("")) {
                    roomManager?.setMaxSuggestions(roomCode, MaxSuggestions.toInt())
                }
            }
        )
    )
    Button(
        onClick = {
            if (activity != null) {
                HideSoftKeyboard.hideSoftKeyboard(activity = activity)
            }
            if (MaxSuggestions.isDigitsOnly() && !MaxSuggestions.equals("")) {
                roomManager?.setMaxSuggestions(roomCode, MaxSuggestions.toInt())
            }
        },
        enabled = MaxSuggestions.isNotEmpty()
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
            maxUpvotes = MutableStateFlow(1),
            maxSuggestions = MutableStateFlow(1),
            activity = null
        )
    }
}
