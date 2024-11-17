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

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.google.jetstream.data.entities.MovieDetails
import com.google.jetstream.data.enum.MediaType
import com.google.jetstream.data.repositories.MovieRepository
import com.google.jetstream.data.room.dao.MovieProgressDao
import com.google.jetstream.data.room.entities.MovieProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class VideoPlayerScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MovieRepository,
    @ApplicationContext context: Context,
    val player: ExoPlayer,
    val movieProgressDao: MovieProgressDao,

) : ViewModel() {

    init {
        player.prepare()
        trackPlayerPosition()
    }

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _subtitlesVisible = MutableStateFlow(true)
    val subtitlesVisible: StateFlow<Boolean> = _subtitlesVisible

    val uiState = savedStateHandle
        .getStateFlow<String?>(VideoPlayerScreen.MovieIdBundleKey, null)
        .combine(
            savedStateHandle.getStateFlow<Boolean?>(
                VideoPlayerScreen.StartFromBeginningKey,
                null
            )
        ) { id, startFromBeginning ->
            if (id == null || startFromBeginning == null) {
                VideoPlayerScreenUiState.Error
            } else {
                val details = repository.getMovieDetails(movieId = id)
                VideoPlayerScreenUiState.Done(
                    movieDetails = details,
                    startFromBeginning = startFromBeginning
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VideoPlayerScreenUiState.Loading
        )

    fun toggleSubtitlesVisibility() {
        Log.d("VideoPlayerScreenViewModel", "Subtitles visibility toggled")
        _subtitlesVisible.value = !_subtitlesVisible.value
    }

    fun playVideo(movieDetails: MovieDetails, startFromBeginning: Boolean) {
        viewModelScope.launch {
            // Set up ExoPlayer with media item and subtitles
            val mediaItem = MediaItem.Builder()
                .setUri(Uri.fromFile(File(movieDetails.videoUri)))
                .setSubtitleConfigurations(
                    listOf(
                        MediaItem.SubtitleConfiguration.Builder(Uri.fromFile(movieDetails.subtitleUri?.let {
                            File(
                                it
                            )
                        }))
                            .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                            .setLanguage("en")
                            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                            .build()
                    )
                ).build()

            val movieProgress = movieProgressDao.getMovieProgress(movieDetails.id)

            player.setMediaItem(mediaItem)
            if (movieProgress != null && !startFromBeginning) {
                player.seekTo(movieProgress)
            }

            addMediaItems()

            player.play()
        }
    }

    fun addMediaItems() {
        val mediaItem = MediaItem.Builder()
            .setUri(Uri.fromFile(File("/storage/emulated/0/Android/data/com.google.jetstream/files/The Office S1E1.mkv")))
            .build()

        val mediaItem2 = MediaItem.Builder()
            .setUri(Uri.fromFile(File("/storage/emulated/0/Android/data/com.google.jetstream/files/The Office S1E2.mkv")))
            .build()

        player.addMediaItem(mediaItem)
        player.addMediaItem(mediaItem2)

//        player.addListener(object : Player.Listener {
//            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
//                Log.d("VideoPlayerScreenViewModel", "Media item transition: $mediaItem")
//                viewModelScope.launch {
//                    try {
//                        val mediaId = mediaItem?.mediaId
//                        if (mediaId != null) {
//                            val movieProgress = movieProgressDao.getMovieProgress(mediaId)
//                            if (movieProgress != null) {
//                                player.seekTo(movieProgress)
//                            }
//                        }
//                    } catch (e: Exception) {
//                        // Handle network errors
//                    }
//                }
//            }
//        })
    }

    private fun trackPlayerPosition() {
        viewModelScope.launch {
            while (true) {
                delay(300)
                _currentPosition.value = player.currentPosition
                _isPlaying.value = player.isPlaying
            }
        }
    }

    fun saveCurrentPosition(movieDetails: MovieDetails, progress: Long) {
        viewModelScope.launch {
            Log.d("VideoPlayerScreenViewModel", "Saving current position: $progress")
            movieProgressDao.upsertMovieProgress(MovieProgress(movieDetails.id, progress))
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
        Log.d("VideoPlayerScreenViewModel", "Player released")
    }
}

@Immutable
sealed class VideoPlayerScreenUiState {
    object Loading : VideoPlayerScreenUiState()
    object Error : VideoPlayerScreenUiState()
    data class Done(val movieDetails: MovieDetails, val startFromBeginning: Boolean) :
        VideoPlayerScreenUiState()
}
