package com.example.demortc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demortc.ui.theme.DemoRTCTheme
import io.getstream.video.android.compose.permission.LaunchCallPermissions
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.FlipCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.call.renderer.FloatingParticipantVideo
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.RealtimeConnection
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiKey = "mmhfdzb5evj2"
        val userToken =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3Byb250by5nZXRzdHJlYW0uaW8iLCJzdWIiOiJ1c2VyL1dhdHRvIiwidXNlcl9pZCI6IldhdHRvIiwidmFsaWRpdHlfaW5fc2Vjb25kcyI6NjA0ODAwLCJpYXQiOjE3NDAwNzI5ODksImV4cCI6MTc0MDY3Nzc4OX0.zy253Q9M4V9rXNg8mo_KI12lyAgKUnG88DODh33zruo"
        val userId = "Watto"
        val callId = "nqYDEmyCeL74"

        // Create a user.
        val user = User(
            id = userId, // any string
            name = "Tutorial", // name and image are used in the UI
            image = "https://bit.ly/2TIt8NR",
        )

        // Initialize StreamVideo. For a production app, we recommend adding the client to your Application class or di module.
        val client = StreamVideoBuilder(
            context = applicationContext,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            user = user,
            token = userToken,
        ).build()

        setContent {
            // Request permissions and join a call, which type is `default` and id is `123`.
            val call = client.call(type = "default", id = callId)
            LaunchCallPermissions(
                call = call,
                onAllPermissionsGranted = {
                    // All permissions are granted so that we can join the call.
                    val result = call.join(create = true)
                    result.onError {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            )

            // Apply VideoTheme
//            VideoTheme {
//                val remoteParticipants by call.state.remoteParticipants.collectAsState()
//                val remoteParticipant = remoteParticipants.firstOrNull()
//                val me by call.state.me.collectAsState()
//                val connection by call.state.connection.collectAsState()
//                var parentSize: IntSize by remember { mutableStateOf(IntSize(0, 0)) }
//
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(VideoTheme.colors.baseSenary)
//                        .onSizeChanged { parentSize = it }
//                ) {
//                    if (remoteParticipant != null) {
//                        ParticipantVideo(
//                            modifier = Modifier.fillMaxSize(),
//                            call = call,
//                            participant = remoteParticipant
//                        )
//                    } else {
//                        if (connection != RealtimeConnection.Connected) {
//                            Text(
//                                text = "waiting for a remote participant...",
//                                fontSize = 30.sp,
//                                color = VideoTheme.colors.basePrimary
//                            )
//                        } else {
//                            Text(
//                                modifier = Modifier.padding(30.dp),
//                                text = "Join call ${call.id} in your browser to see the video here",
//                                fontSize = 30.sp,
//                                color = VideoTheme.colors.basePrimary,
//                                textAlign = TextAlign.Center
//                            )
//                        }
//                    }
//
//                    // floating video UI for the local video participant
//                    me?.let { localVideo ->
//                        FloatingParticipantVideo(
//                            modifier = Modifier.align(Alignment.TopEnd),
//                            call = call,
//                            participant = localVideo,
//                            parentBounds = parentSize
//                        )
//                    }
//                }
//            }
//        }

//            VideoTheme {
//                CallContent(
//                    modifier = Modifier.fillMaxSize(),
//                    call = call,
//                    onBackPressed = { onBackPressed() },
//                )
//            }

            VideoTheme {
                val isCameraEnabled by call.camera.isEnabled.collectAsState()
                val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()

                CallContent(
                    modifier = Modifier.background(color = Color.White),
                    call = call,
                    onBackPressed = { onBackPressed() },
                    controlsContent = {
                        ControlActions(
                            call = call,
                            actions = listOf(
                                {
                                    ToggleCameraAction(
                                        modifier = Modifier.size(52.dp),
                                        isCameraEnabled = isCameraEnabled,
                                        onCallAction = { call.camera.setEnabled(it.isEnabled) }
                                    )
                                },
                                {
                                    ToggleMicrophoneAction(
                                        modifier = Modifier.size(52.dp),
                                        isMicrophoneEnabled = isMicrophoneEnabled,
                                        onCallAction = { call.microphone.setEnabled(it.isEnabled) }
                                    )
                                },
                                {
                                    FlipCameraAction(
                                        modifier = Modifier.size(52.dp),
                                        onCallAction = { call.camera.flip() }
                                    )
                                },
                            )
                        )
                    }
                )
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        DemoRTCTheme {
            Greeting("Android")
        }
    }
}