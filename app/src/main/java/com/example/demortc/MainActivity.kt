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
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            var isCallStarted by remember { mutableStateOf(false) }

            val call = client.call(type = "default", id = callId)

            if (!isCallStarted) {
                // Show "Start Video Call" button
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start Video Call",
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color.Blue)
                            .padding(16.dp)
                            .clickable {
                                isCallStarted = true
                                coroutineScope.launch {
                                    val result = call.join(create = true)
                                    result.onError {
                                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                    )
                }
            } else {
                // Video call UI after clicking the button
                VideoTheme {
                    val isCameraEnabled by call.camera.isEnabled.collectAsState()
                    val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()

                    CallContent(
                        modifier = Modifier.background(color = Color.White),
                        call = call,
                        appBarContent = {},
                        onBackPressed = {
                            coroutineScope.launch {
                                call.leave()
                                finishAffinity()
                            }
                        },
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
                                    {
                                        // Custom Hang-Up Button
                                        Text(
                                            text = "Hang Up",
                                            color = Color.Red,
                                            fontSize = 18.sp,
                                            modifier = Modifier
                                                .clickable {
                                                    coroutineScope.launch {
                                                        call.leave()
                                                        finishAffinity()
                                                    }
                                                }
                                                .padding(10.dp)
                                        )
                                    }
                                )
                            )
                        }
                    )
                }
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