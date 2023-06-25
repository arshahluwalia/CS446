package com.example.jukebox//package com.example.jukebox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.jukebox.spotify.SpotifyAccessTokenTask
import com.example.jukebox.ui.theme.JukeboxTheme
import com.example.jukebox.ui.theme.PurpleNeon
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse


class AuthorizeActivity : ComponentActivity() {
    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.

    private val clientID = BuildConfig.SPOTIFY_CLIENT_ID;
    private val requestCode = 1335; // Could be any we choose
    private val redirectUri = "jukebox://callback"; //the one we registered with Spotify

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JukeboxTheme() {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ScreenContent()
                }
            }
        }
    }

    @Composable
    private fun ScreenContent() {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenBackground()
            MoveOnButton()
            AuthorizeSpotifyButton()
        }
    }

    @Composable
    fun BoxWithConstraintsScope.ScreenBackground() {
        Box(
            modifier = Modifier.reusableBackground()
        )
    }
    @Composable
    fun BoxWithConstraintsScope.AuthorizeSpotifyButton(){
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
                    onRequestTokenClicked()
//                val intent = Intent(context, HostSongQueueActivity::class.java)
//                context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text(
                    text = AnnotatedString("Login to Spotify"),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
    }

    // TODO: Remove when authorization is done
    @Composable
    fun BoxWithConstraintsScope.MoveOnButton() {
        val context = LocalContext.current
        Button(
            modifier = Modifier
                .padding(bottom = maxHeight / 5)
                .align(Alignment.BottomCenter),
            onClick = {
                SpotifyAccessTokenTask.requestAccessToken()
                val intent = Intent(context, HostSongQueueActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon)
        ) {
            Text(
                text = AnnotatedString("Go to host queue"),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }

    private fun onRequestTokenClicked() {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN)
        AuthorizationClient.openLoginActivity(this, requestCode, request)
    }

    private fun getAuthenticationRequest(type: AuthorizationResponse.Type): AuthorizationRequest? {
        return AuthorizationRequest.Builder(clientID, type, getRedirectUri().toString())
            .setShowDialog(false)
            .setScopes(arrayOf<String>("user-read-playback-state", "user-modify-playback-state",
                "user-read-currently-playing",
                "app-remote-control",
                "streaming",
                "playlist-modify-public"))
            .setCampaign("your-campaign-token")
            .build()
    }

    private fun getRedirectUri(): Uri? {
        return Uri.parse(redirectUri)
    }

//    AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
//    private static final int REQUEST_CODE = 1337;
//    private static final String REDIRECT_URI = "yourcustomprotocol://callback";
//
//    AuthorizationRequest.Builder builder =
//    new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
//
//    builder.setScopes(new String[]{"streaming"});
//    AuthorizationRequest request = builder.build();
//
//    AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

}