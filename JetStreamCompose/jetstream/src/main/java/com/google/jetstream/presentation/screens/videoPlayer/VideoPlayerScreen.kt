/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.jetstream.presentation.screens.videoPlayer

import android.net.Uri
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.text.Cue
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import androidx.media3.ui.SubtitleView
import androidx.tv.material3.Text
import com.google.jetstream.R
import com.google.jetstream.data.entities.MovieDetails
import com.google.jetstream.data.util.StringConstants
import com.google.jetstream.presentation.common.Error
import com.google.jetstream.presentation.common.Loading
import com.google.jetstream.presentation.screens.videoPlayer.components.VideoPlayerControlsIcon
import com.google.jetstream.presentation.screens.videoPlayer.components.VideoPlayerMainFrame
import com.google.jetstream.presentation.screens.videoPlayer.components.VideoPlayerMediaTitle
import com.google.jetstream.presentation.screens.videoPlayer.components.VideoPlayerMediaTitleType
import com.google.jetstream.presentation.screens.videoPlayer.components.VideoPlayerOverlay
import com.google.jetstream.presentation.screens.videoPlayer.components.VideoPlayerPulse
import com.google.jetstream.presentation.screens.videoPlayer.components.VideoPlayerPulse.Type.BACK
import com.google.jetstream.presentation.screens.videoPlayer.components.VideoPlayerPulse.Type.FORWARD
import com.google.jetstream.presentation.screens.videoPlayer.components.VideoPlayerPulseState
import com.google.jetstream.presentation.screens.videoPlayer.components.VideoPlayerSeeker
import com.google.jetstream.presentation.screens.videoPlayer.components.VideoPlayerState
import com.google.jetstream.presentation.screens.videoPlayer.components.rememberVideoPlayerPulseState
import com.google.jetstream.presentation.screens.videoPlayer.components.rememberVideoPlayerState
import com.google.jetstream.presentation.theme.Typography
import com.google.jetstream.presentation.utils.handleDPadKeyEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

object VideoPlayerScreen {
    const val MovieIdBundleKey = "movieId"
}

/**
 * [Work in progress] A composable screen for playing a video.
 *
 * @param onBackPressed The callback to invoke when the user presses the back button.
 * @param videoPlayerScreenViewModel The view model for the video player screen.
 */
@Composable
fun VideoPlayerScreen(
    onBackPressed: () -> Unit,
    videoPlayerScreenViewModel: VideoPlayerScreenViewModel = hiltViewModel()
) {
    val uiState by videoPlayerScreenViewModel.uiState.collectAsStateWithLifecycle()

    // TODO: Handle Loading & Error states
    when (val s = uiState) {
        is VideoPlayerScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is VideoPlayerScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }

        is VideoPlayerScreenUiState.Done -> {
            VideoPlayerScreenContent(
                movieDetails = s.movieDetails,
                onBackPressed = onBackPressed
            )
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreenContent(movieDetails: MovieDetails, onBackPressed: () -> Unit) {
    val viewModel = hiltViewModel<VideoPlayerScreenViewModel>()
    val context = LocalContext.current
    val videoPlayerState = rememberVideoPlayerState(hideSeconds = 4)

    var subtitleText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    var subtitlesVisible by remember { mutableStateOf(true) }

    // Initialize ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.Builder()
                .setUri(Uri.fromFile(File("/storage/emulated/0/Android/data/com.google.jetstream/files/Office Space.mkv")))
                .setSubtitleConfigurations(
                    listOf(
                        MediaItem.SubtitleConfiguration.Builder(Uri.fromFile(File("/storage/emulated/0/Android/data/com.google.jetstream/files/Office.Space.srt")))
                            .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                            .setLanguage("en")
                            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                            .build()
                    )
                ).build()

            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true

            // Add a listener for subtitles using the new onCues() method
            addListener(object : Player.Listener {
                override fun onCues(cues: List<Cue>) {
                    // Update subtitle text when new cues are received
                    scope.launch(Dispatchers.Main) {
                        subtitleText = cues.joinToString("\n") { it.text.toString() }
                    }
                }
            })
        }
    }


    var contentCurrentPosition by remember { mutableLongStateOf(0L) }
    var isPlaying: Boolean by remember { mutableStateOf(exoPlayer.isPlaying) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(300)
            contentCurrentPosition = exoPlayer.currentPosition
            isPlaying = exoPlayer.isPlaying
        }
    }


    BackHandler(onBack = onBackPressed)

    val pulseState = rememberVideoPlayerPulseState()

    Box(
        Modifier
            .dPadEvents(
                exoPlayer,
                videoPlayerState,
                pulseState
            )
            .focusable()
    ) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    useController = false
                    subtitleView?.apply {
                        // Set the style for the subtitle
                        updateSubtitleVisibility(subtitlesVisible)

                        setFractionalTextSize(0.04f)
                        setBottomPaddingFraction(0.15f)  // Move subtitles up by 15% of the screen height
                    }
                }
            },
            update = {
                it.player = exoPlayer
                it.subtitleView?.updateSubtitleVisibility(subtitlesVisible)
            },
            onRelease = { exoPlayer.release() },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
        )

        val focusRequester = remember { FocusRequester() }
        VideoPlayerOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
            focusRequester = focusRequester,
            state = videoPlayerState,
            isPlaying = isPlaying,
            centerButton = { VideoPlayerPulse(pulseState) },
            subtitles = {},
            controls = {
                VideoPlayerControls(
                    movieDetails,
                    isPlaying,
                    contentCurrentPosition,
                    exoPlayer,
                    videoPlayerState,
                    focusRequester,
                    {
                        subtitlesVisible = !subtitlesVisible
                    }
                )
            }
        )
    }
}

@Composable
fun SubtitleOverlay(subtitleText: String, modifier: Modifier = Modifier) {
    if (subtitleText.isNotEmpty()) {
        Box(
            modifier = modifier
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.7f)), // Black background with some transparency
            contentAlignment = Alignment.Center // Center the text
        ) {
            Text(
                text = subtitleText,
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp) // Padding for the text
            )
        }
    }
}

@Composable
fun VideoPlayerControls(
    movieDetails: MovieDetails,
    isPlaying: Boolean,
    contentCurrentPosition: Long,
    exoPlayer: ExoPlayer,
    state: VideoPlayerState,
    focusRequester: FocusRequester,
    toggleSubtitles: () -> Unit
) {
    val onPlayPauseToggle = { shouldPlay: Boolean ->
        if (shouldPlay) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    VideoPlayerMainFrame(
        mediaTitle = {
            VideoPlayerMediaTitle(
                title = movieDetails.name,
                secondaryText = movieDetails.releaseDate,
                tertiaryText = movieDetails.director,
                type = VideoPlayerMediaTitleType.DEFAULT
            )
        },
        mediaActions = {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VideoPlayerControlsIcon(
                    icon = Icons.Default.AutoAwesomeMotion,
                    state = state,
                    isPlaying = isPlaying,
                    contentDescription = StringConstants
                        .Composable
                        .VideoPlayerControlPlaylistButton
                )
                VideoPlayerControlsIcon(
                    modifier = Modifier.padding(start = 12.dp),
                    icon = Icons.Default.ClosedCaption,
                    state = state,
                    isPlaying = isPlaying,
                    contentDescription = StringConstants
                        .Composable
                        .VideoPlayerControlClosedCaptionsButton,
                    onClick = {
                        toggleSubtitles()
                    }
                )
                VideoPlayerControlsIcon(
                    modifier = Modifier.padding(start = 12.dp),
                    icon = Icons.Default.Settings,
                    state = state,
                    isPlaying = isPlaying,
                    contentDescription = StringConstants
                        .Composable
                        .VideoPlayerControlSettingsButton
                )
            }
        },
        seeker = {
            VideoPlayerSeeker(
                focusRequester,
                state,
                isPlaying,
                onPlayPauseToggle,
                onSeek = { exoPlayer.seekTo(exoPlayer.duration.times(it).toLong()) },
                contentProgress = contentCurrentPosition.milliseconds,
                contentDuration = exoPlayer.duration.milliseconds
            )
        },
        more = null
    )
}

private fun Modifier.dPadEvents(
    exoPlayer: ExoPlayer,
    videoPlayerState: VideoPlayerState,
    pulseState: VideoPlayerPulseState
): Modifier = this.handleDPadKeyEvents(
    onLeft = {
        if (!videoPlayerState.controlsVisible) {
            exoPlayer.seekBack()
            pulseState.setType(BACK)
        }
    },
    onRight = {
        if (!videoPlayerState.controlsVisible) {
            exoPlayer.seekForward()
            pulseState.setType(FORWARD)
        }
    },
    onFastForward = {
        exoPlayer.seekForward()
        pulseState.setType(FORWARD)
        videoPlayerState.showControls()
    },
    onRewind = {
        exoPlayer.seekBack()
        pulseState.setType(BACK)
        videoPlayerState.showControls()
    },
    onUp = { videoPlayerState.showControls() },
    onDown = { videoPlayerState.showControls() },
    onEnter = {
        exoPlayer.pause()
        videoPlayerState.showControls()
    },
    onPlayPause = {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            videoPlayerState.showControls()
        } else {
            exoPlayer.play()
            videoPlayerState.showControls()
        }
    }
)

// Extension function to update subtitle visibility
@OptIn(UnstableApi::class)
fun SubtitleView.updateSubtitleVisibility(isVisible: Boolean) {
    if (isVisible) {
        setStyle(
            CaptionStyleCompat(
                Color.White.toArgb(),  // Foreground text color from Compose
                Color.Black.copy(alpha = 0.5f)
                    .toArgb(),  // Background color from Compose
                Color.Transparent.toArgb(),  // Window color (transparent)
                CaptionStyleCompat.EDGE_TYPE_OUTLINE,  // Text outline
                Color.Black.toArgb(),  // Outline color from Compose
                ResourcesCompat.getFont(
                    context,
                    R.font.inter_regular
                ) // Custom typeface (can be set to null for default)
            )
        )
    } else {
        setStyle(
            CaptionStyleCompat(
                Color.Transparent.toArgb(),  // Hide text by making it transparent
                Color.Transparent.toArgb(),  // Hide background
                CaptionStyleCompat.EDGE_TYPE_NONE,
                Color.Transparent.toArgb(),
                Color.Transparent.toArgb(),
                null
            )
        )
    }
}
