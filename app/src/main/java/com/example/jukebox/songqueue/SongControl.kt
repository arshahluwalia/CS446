package com.example.jukebox.songqueue

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.jukebox.R


@Composable
fun SongControl() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //TODO: Fix the weird shapes of next and prev button
        IconButton(
            onClick = {
                //TODO: https://api.spotify.com/v1/me/player/previous - wip in SpotifyApi
            }) {
            Image(painter = painterResource(id = R.drawable.previous_track), contentDescription = null)
        }
        IconButton(
            onClick = {
                //TODO: https://api.spotify.com/v1/me/player/play
            }) {
            Image(painter = painterResource(id = R.drawable.pause_track), contentDescription = null)
        }
        IconButton(
            onClick = {
                //TODO: https://api.spotify.com/v1/me/player/next
            }) {
            Image(painter = painterResource(id = R.drawable.next_track), contentDescription = null)
        }
    }
}


