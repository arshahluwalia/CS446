package com.example.jukebox

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
        getMaxUpvotes(roomCode, maxUpvotes)
        val maxSuggestions = MutableStateFlow(1)
        getMaxSuggestions(roomCode, maxSuggestions)
        val limitUpvotes = MutableStateFlow(true)
        getLimitUpvotes(roomCode, limitUpvotes)
        val limitSuggestions = MutableStateFlow(true)
        getLimitSuggestions(roomCode, limitSuggestions)
        val autoRemove = MutableStateFlow(true)
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
                    limitSuggestions = limitSuggestions,
                    limitUpvotes = limitUpvotes,
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

    private fun getLimitSuggestions(roomCode: String, limitSuggestions: MutableStateFlow<Boolean>) {
        val roomManager = RoomManager()
        roomManager.getLimitSuggestions(roomCode) {
            limitSuggestions.value = it
        }
    }

    private fun getLimitUpvotes(roomCode: String, limitUpvotes: MutableStateFlow<Boolean>) {
        val roomManager = RoomManager()
        roomManager.getLimitUpvotes(roomCode) {
            limitUpvotes.value = it
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
    limitSuggestions: MutableStateFlow<Boolean>,
    limitUpvotes: MutableStateFlow<Boolean>,
    activity: Activity?
) {
    var isNameChanged by remember { mutableStateOf(false) }
    var updatedName by remember { mutableStateOf("") }

    var isSuggestionsToggleChanged by remember { mutableStateOf(false) }
    var limitSuggestionsEnabled by remember { mutableStateOf(true) }

    var isSuggestionsChanged by remember { mutableStateOf(false) }
    var updatedSuggestions by remember { mutableStateOf(5) }

    var isUpvotesToggleChanged by remember { mutableStateOf(false) }
    var limitUpvotesEnabled by remember { mutableStateOf(true) }

    var isUpvotesChanged by remember { mutableStateOf(false) }
    var updatedUpvotes by remember { mutableStateOf(5) }

    var isAutoRemoveToggleChanged by remember { mutableStateOf(false) }
    var autoRemoveEnabled by remember { mutableStateOf(true) }

    Box {
        SecondaryBackground()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            BackToQueueButton(dispatcher)
        }

        Row(
            modifier = Modifier
                .padding(top = 35.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SettingsTitle()
        }

        Column(
            modifier = Modifier
                .padding(top = 100.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChangeNameField(
                currentHostName = hostName,
                onValueChanged = {
                    isNameChanged = true
                    updatedName = it
                },
                activity = activity
            )

            ToggleChangeSuggestions(
                roomCode = roomCode,
                roomManager = roomManager,
                limitSuggestions = limitSuggestions,
                onValueChanged = {
                    isSuggestionsToggleChanged = true
                    limitSuggestionsEnabled = it
                }
            )

            if (limitSuggestions.collectAsState().value) {
                ChangeMaxSuggestions(
                    limitSuggestions = limitSuggestions,
                    maxSuggestions = maxSuggestions,
                    onValueChanged = {
                        isSuggestionsChanged = true
                        updatedSuggestions = it.toInt()
                    }
                )
            }

            ToggleChangeUpvotes(
                roomCode = roomCode,
                roomManager = roomManager,
                limitUpvotes = limitUpvotes,
                onValueChanged = {
                    isUpvotesToggleChanged = true
                    limitUpvotesEnabled = it
                }
            )

            if (limitUpvotes.collectAsState().value) {
                ChangeMaxUpvotes(
                    limitUpvotes = limitUpvotes,
                    maxUpvotes = maxUpvotes,
                    onValueChanged = {
                        isUpvotesChanged = true
                        updatedUpvotes = it.toInt()
                    }
                )
            }

            ToggleAutoRemove(
                autoRemove = autoRemove,
                onValueChanged = {
                    isAutoRemoveToggleChanged = true
                    autoRemoveEnabled = it
                }
            )

            Button(
                modifier = Modifier.padding(top = 10.dp),
                onClick = {
                    if (isNameChanged) {
                        roomManager?.setHostName(roomCode, updatedName)
                        isNameChanged = false
                    }

                    if (isSuggestionsToggleChanged) {
                        roomManager?.setLimitSuggestions(roomCode, limitSuggestionsEnabled)
                        if (!limitSuggestionsEnabled) {
                            roomManager?.setMaxSuggestions(roomCode, 99999)
                            isSuggestionsChanged = false
                        }
                        isSuggestionsToggleChanged = false
                    }

                    if (isSuggestionsChanged) {
                        if (updatedSuggestions.toString().isDigitsOnly() && updatedSuggestions.toString() != "") {
                            roomManager?.setMaxSuggestions(roomCode, updatedSuggestions)
                        }
                        isSuggestionsChanged = false
                    }

                    if (isUpvotesToggleChanged) {
                        roomManager?.setLimitUpvotes(roomCode, limitUpvotesEnabled)
                        if (!limitUpvotesEnabled) {
                            roomManager?.setMaxUpvotes(roomCode, 99999)
                            isUpvotesChanged = false
                        }
                        isUpvotesToggleChanged = false
                    }

                    if (isUpvotesChanged) {
                        if (updatedUpvotes.toString().isDigitsOnly() && updatedUpvotes.toString() != "") {
                            roomManager?.setMaxUpvotes(roomCode, updatedUpvotes)
                        }
                        isUpvotesChanged = false
                    }

                    if (isAutoRemoveToggleChanged) {
                        roomManager?.setAutoRemove(roomCode, autoRemoveEnabled)
                        isAutoRemoveToggleChanged = false
                    }
                },
                enabled = isNameChanged || isSuggestionsToggleChanged || isSuggestionsChanged || isUpvotesToggleChanged || isUpvotesChanged || isAutoRemoveToggleChanged
            ) {
                Text(text = "Save")
            }
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
    currentHostName: MutableStateFlow<String>,
    onValueChanged: (String) -> Unit,
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
                onValueChanged(hostName)
            }
        )
    )
}

@Composable
private fun ChangeMaxUpvotes(
    limitUpvotes: MutableStateFlow<Boolean>,
    maxUpvotes: MutableStateFlow<Int>,
    onValueChanged: (String) -> Unit
) {
    val limitUpvotesFlag = limitUpvotes.collectAsState().value
    val maxUpvotesInt = maxUpvotes.collectAsState().value
    var maxUpvotesString by remember { mutableStateOf(maxUpvotesInt.toString()) }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(maxUpvotesInt) {
        if (limitUpvotesFlag && maxUpvotesInt > 0 && maxUpvotesInt < 101) {
            lazyListState.scrollToItem(maxUpvotesInt - 1)
        } else {
            lazyListState.scrollToItem(0)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .height(70.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        userScrollEnabled = limitUpvotesFlag
    ) {
        items(101) { page ->
            val textColor = if (limitUpvotesFlag && page.toString() == maxUpvotesString) Color.White else Color.Gray

            Text(
                modifier = Modifier.clickable {
                    if (limitUpvotesFlag) {
                        maxUpvotesString = page.toString()
                        onValueChanged(maxUpvotesString)
                    }
                },
                text = page.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        }
    }

    LaunchedEffect(maxUpvotesInt) {
        maxUpvotesString = maxUpvotesInt.toString()
    }
}

@Composable
private fun ChangeMaxSuggestions(
    limitSuggestions: MutableStateFlow<Boolean>,
    maxSuggestions: MutableStateFlow<Int>,
    onValueChanged: (String) -> Unit
) {
    val limitSuggestionsFlag = limitSuggestions.collectAsState().value
    val maxSuggestionsInt = maxSuggestions.collectAsState().value
    var maxSuggestionsString by remember { mutableStateOf(maxSuggestionsInt.toString()) }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(maxSuggestionsInt) {
        if (limitSuggestionsFlag && maxSuggestionsInt > 0 && maxSuggestionsInt < 101) {
            lazyListState.scrollToItem(maxSuggestionsInt - 1)
        } else {
            lazyListState.scrollToItem(0)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .height(70.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        userScrollEnabled = limitSuggestionsFlag
    ) {
        items(101) { page ->
            val textColor = if (limitSuggestionsFlag && page.toString() == maxSuggestionsString) Color.White else Color.Gray

            Text(
                modifier = Modifier.clickable {
                    if (limitSuggestionsFlag) {
                        maxSuggestionsString = page.toString()
                        onValueChanged(maxSuggestionsString)
                    }
                },
                text = page.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        }
    }

    LaunchedEffect(maxSuggestionsInt) {
        maxSuggestionsString = maxSuggestionsInt.toString()
    }
}

@Composable
private fun ToggleAutoRemove(
    autoRemove: MutableStateFlow<Boolean>,
    onValueChanged: (Boolean) -> Unit
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
            onValueChanged(it)
        }
    )

    LaunchedEffect(autoRemoveValue) {
        autoRemoveEnabled = autoRemoveValue
    }
}

@Composable
private fun ToggleChangeSuggestions(
    roomCode: String,
    roomManager: RoomManager?,
    limitSuggestions: MutableStateFlow<Boolean>,
    onValueChanged: (Boolean) -> Unit
) {
    val toggleValue = limitSuggestions.collectAsState().value
    var toggleEnabled by remember { mutableStateOf(toggleValue) }

    Row(
        modifier = Modifier.padding(end = 120.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth()
                .padding(start = 60.dp),
            text = "Limit Suggestions:",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.Start
        )

        Switch(
            checked = toggleEnabled,
            onCheckedChange = {
                toggleEnabled = it
                onValueChanged(it)

                roomManager?.setLimitSuggestions(roomCode, it)
                if (!it) {
                    roomManager?.setMaxSuggestions(roomCode, 99999)
                }
            }
        )
    }

    LaunchedEffect(toggleValue) {
        toggleEnabled = toggleValue
    }
}

@Composable
private fun ToggleChangeUpvotes(
    roomCode: String,
    roomManager: RoomManager?,
    limitUpvotes: MutableStateFlow<Boolean>,
    onValueChanged: (Boolean) -> Unit
) {
    val toggleValue = limitUpvotes.collectAsState().value
    var toggleEnabled by remember { mutableStateOf(toggleValue) }

    Row(
        modifier = Modifier.padding(end = 120.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth()
                .padding(start = 60.dp),
            text = "Limit Upvotes:",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.Start
        )

        Switch(
            checked = toggleEnabled,
            onCheckedChange = {
                toggleEnabled = it
                onValueChanged(it)

                roomManager?.setLimitUpvotes(roomCode, it)
                if (!it) {
                    roomManager?.setMaxUpvotes(roomCode, 99999)
                }
            }
        )
    }

    LaunchedEffect(toggleValue) {
        toggleEnabled = toggleValue
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
            limitSuggestions = MutableStateFlow(true),
            limitUpvotes = MutableStateFlow(true),
            activity = null
        )
    }
}
