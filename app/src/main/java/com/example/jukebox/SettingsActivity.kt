package com.example.jukebox

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

        val autoRemove = MutableStateFlow(false)
        getAutoRemove(roomCode, autoRemove)
        setContent {
            JukeboxTheme {
                ScreenContent(
                    dispatcher = dispatcher,
                    roomCode = roomCode,
                    roomManager = roomManager,
                    hostName = hostName,
                    maxUpvotes = maxUpvotes,
                    maxSuggestions = maxSuggestions,
                    autoRemove = autoRemove,
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

    private fun getAutoRemove(roomCode: String, autoRemove: MutableStateFlow<Boolean>) {
        val roomManager = RoomManager()
        roomManager.getAutoRemove(roomCode) {
            autoRemove.value = it
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
    autoRemove: MutableStateFlow<Boolean>,
    activity: Activity?
) {
    Box {
        SecondaryBackground()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            BackToQueueButton(dispatcher)
        }

        Row(
            modifier = Modifier.padding(top = 35.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SettingsTitle()
        }

        Column(
            modifier = Modifier.padding(top = 100.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            ToggleAutoRemove(
                roomCode = roomCode,
                roomManager = roomManager,
                autoRemove = autoRemove
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
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth()
            .padding(start = 60.dp),
        text = "Name:",
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
    val maxUpvotesInt = maxUpvotes.collectAsState().value
    var maxUpvotesString by remember { mutableStateOf(maxUpvotesInt.toString()) }

    Text(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth()
            .padding(start = 60.dp),
        text = "Maximum Upvotes:",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White,
        textAlign = TextAlign.Start
    )

    val lazyListState = rememberLazyListState()
    val preselectedItem = maxUpvotesInt

    LaunchedEffect(preselectedItem) {
        if (preselectedItem > 0) {
            lazyListState.scrollToItem(preselectedItem - 1)
        } else {
            lazyListState.scrollToItem(preselectedItem)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .height(70.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(101) { page ->
            val textColor = if (page.toString() == maxUpvotesString) Color.White else Color.Gray

            Text(
                modifier = Modifier.clickable {
                    maxUpvotesString = page.toString()
                },
                text = page.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        }
    }

    Button(
        onClick = {
            if (activity != null) {
                HideSoftKeyboard.hideSoftKeyboard(activity = activity)
            }
            if (maxUpvotesString.isDigitsOnly() && maxUpvotesString != "") {
                roomManager?.setMaxUpvotes(roomCode, maxUpvotesString.toInt())
            }
        },
        enabled = maxUpvotesString.toInt() != maxUpvotesInt
    ) {
        Text(text = "Save")
    }

    LaunchedEffect(maxUpvotesInt) {
        maxUpvotesString = maxUpvotesInt.toString()
    }
}

@Composable
private fun ChangeMaxSuggestions(
    roomCode: String,
    roomManager: RoomManager?,
    maxSuggestions: MutableStateFlow<Int>,
    activity: Activity?
) {
    val maxSuggestionsInt = maxSuggestions.collectAsState().value
    var maxSuggestionsString by remember { mutableStateOf(maxSuggestionsInt.toString()) }

    Text(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth()
            .padding(start = 60.dp),
        text = "Maximum Suggestions:",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White,
        textAlign = TextAlign.Start
    )

    val lazyListState = rememberLazyListState()
    val preselectedItem = maxSuggestionsInt

    LaunchedEffect(preselectedItem) {
        if (preselectedItem > 0) {
            lazyListState.scrollToItem(preselectedItem - 1)
        } else {
            lazyListState.scrollToItem(preselectedItem)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .height(70.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(101) { page ->
            val textColor = if (page.toString() == maxSuggestionsString) Color.White else Color.Gray

            Text(
                modifier = Modifier.clickable {
                    maxSuggestionsString = page.toString()
                },
                text = page.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        }
    }

    Button(
        onClick = {
            if (activity != null) {
                HideSoftKeyboard.hideSoftKeyboard(activity = activity)
            }
            if (maxSuggestionsString.isDigitsOnly() && maxSuggestionsString != "") {
                roomManager?.setMaxSuggestions(roomCode, maxSuggestionsString.toInt())
            }
        },
        enabled = maxSuggestionsString.toInt() != maxSuggestionsInt
    ) {
        Text(text = "Save")
    }

    LaunchedEffect(maxSuggestionsInt) {
        maxSuggestionsString = maxSuggestionsInt.toString()
    }
}

@Composable
private fun ToggleAutoRemove(
    roomCode: String,
    roomManager: RoomManager?,
    autoRemove: MutableStateFlow<Boolean>
) {
    val autoRemoveValue = autoRemove.collectAsState().value
    var autoRemoveEnabled by remember { mutableStateOf(autoRemoveValue) }

    Text(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth()
            .padding(start = 60.dp),
        text = "Auto remove a song when denied:",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White,
        textAlign = TextAlign.Start
    )

    Switch(
        checked = autoRemoveEnabled,
        onCheckedChange = {
            autoRemoveEnabled = it
        }
    )

    Button(
        onClick = {
            roomManager?.setAutoRemove(roomCode, autoRemoveEnabled)
        },
        enabled = autoRemoveEnabled != autoRemoveValue
    ) {
        Text(text = "Save")
    }

    LaunchedEffect(autoRemoveValue) {
        autoRemoveEnabled = autoRemoveValue
    }
}

@Composable
@Preview
private fun PreviewScreenContent() {
    JukeboxTheme {
        ScreenContent(
            dispatcher = null,
            roomCode = "ABCDE",
            hostName = MutableStateFlow("Lucas"),
            roomManager = null,
            maxUpvotes = MutableStateFlow(1),
            maxSuggestions = MutableStateFlow(1),
            autoRemove = MutableStateFlow(false),
            activity = null
        )
    }
}
