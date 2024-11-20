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
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Immutable
import androidx.compose.ui.semantics.getOrNull
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.google.jetstream.data.entities.Episode
import com.google.jetstream.data.entities.EpisodeList
import com.google.jetstream.data.entities.MediaDetails
import com.google.jetstream.data.entities.MovieDetails
import com.google.jetstream.data.enum.MediaType
import com.google.jetstream.data.repositories.MovieRepository
import com.google.jetstream.data.repositories.ShowRepository
import com.google.jetstream.data.room.dao.MovieProgressDao
import com.google.jetstream.data.room.dao.ShowProgressDao
import com.google.jetstream.data.room.entities.MovieProgress
import com.google.jetstream.data.room.entities.ShowProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.collections.indexOfFirst

@UnstableApi
@HiltViewModel
class VideoPlayerScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
    @ApplicationContext context: Context,
    val player: ExoPlayer,
    val movieProgressDao: MovieProgressDao,
    val showProgressDao: ShowProgressDao

) : ViewModel() {

    private val _uiState =
        MutableStateFlow<VideoPlayerScreenUiState>(VideoPlayerScreenUiState.Loading)
    val uiState: StateFlow<VideoPlayerScreenUiState> = _uiState.asStateFlow()

    init {
        player.prepare()
        trackPlayerPosition()

        viewModelScope.launch {
            combine(
                savedStateHandle.getStateFlow<String?>(VideoPlayerScreen.MovieIdBundleKey, null),
                savedStateHandle.getStateFlow<Boolean?>(
                    VideoPlayerScreen.StartFromBeginningKey,
                    null
                ),
                savedStateHandle.getStateFlow<String?>(VideoPlayerScreen.MediaTypeBundleKey, null),
                savedStateHandle.getStateFlow<String?>(VideoPlayerScreen.ShowIdBundleKey, null)
            ) { id, startFromBeginning, mediaType, showId ->
                if (id == null || startFromBeginning == null) {
                    VideoPlayerScreenUiState.Error
                } else {
                    if (mediaType == MediaType.Show.name) {
                        val episodeDetails =
                            showId?.let {
                                showRepository.getEpisodeDetailsByShowIdAndEpisodeId(
                                    it,
                                    id
                                )
                            }
                        episodeDetails?.let {
                            MediaDetails(
                                it.id,
                                episodeDetails.title,
                                episodeDetails.releaseDate,
                                episodeDetails.videoUri,
                                episodeDetails.subtitleUri,
                                showId
                            )
                        }?.let {
                            VideoPlayerScreenUiState.Done(
                                mediaDetails = it,
                                startFromBeginning = startFromBeginning
                            )
                        }
                    } else {
                        val details = movieRepository.getMovieDetails(movieId = id)
                        val mediaDetails = MediaDetails(
                            details.id,
                            details.name,
                            details.releaseDate,
                            details.videoUri,
                            details.subtitleUri,
                            showId = null
                        )
                        VideoPlayerScreenUiState.Done(
                            mediaDetails = mediaDetails,
                            startFromBeginning = startFromBeginning
                        )
                    }
                }
            }.collect { newState ->
                if (newState != null) {
                    _uiState.value = newState
                }
            }
        }
    }

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _subtitlesVisible = MutableStateFlow(true)
    val subtitlesVisible: StateFlow<Boolean> = _subtitlesVisible

//    private val _uiState = combine(
//        savedStateHandle.getStateFlow<String?>(VideoPlayerScreen.MovieIdBundleKey, null),
//        savedStateHandle.getStateFlow<Boolean?>(VideoPlayerScreen.StartFromBeginningKey, null),
//        savedStateHandle.getStateFlow<String?>(
//            VideoPlayerScreen.MediaTypeBundleKey,
//            null
//        ),
//        savedStateHandle.getStateFlow<String?>(VideoPlayerScreen.ShowIdBundleKey, null)
//    ) { id, startFromBeginning, mediaType, showId ->
//        if (id == null || startFromBeginning == null) {
//            VideoPlayerScreenUiState.Error
//        } else {
//            if (mediaType == MediaType.Show.name) {
//                val episodeDetails =
//                    showId?.let { showRepository.getEpisodeDetailsByShowIdAndEpisodeId(it, id) }
//                episodeDetails?.let {
//                    MediaDetails(
//                        it.id,
//                        episodeDetails.title,
//                        episodeDetails.releaseDate,
//                        episodeDetails.videoUri,
//                        episodeDetails.subtitleUri,
//                        showId
//                    )
//                }?.let {
//                    VideoPlayerScreenUiState.Done(
//                        mediaDetails = it,
//                        startFromBeginning = startFromBeginning
//                    )
//                }
//            } else {
//                val details = movieRepository.getMovieDetails(movieId = id)
//                val mediaDetails = MediaDetails(
//                    details.id,
//                    details.name,
//                    details.releaseDate,
//                    details.videoUri,
//                    details.subtitleUri,
//                    showId = null
//                )
//                VideoPlayerScreenUiState.Done(
//                    mediaDetails = mediaDetails,
//                    startFromBeginning = startFromBeginning
//                )
//            }
//        }
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000),
//        initialValue = VideoPlayerScreenUiState.Loading
//    )

    fun toggleSubtitlesVisibility() {
        Log.d("VideoPlayerScreenViewModel", "Subtitles visibility toggled")
        _subtitlesVisible.value = !_subtitlesVisible.value
    }

    fun playVideo(mediaDetails: MediaDetails, startFromBeginning: Boolean, mediaType: MediaType) {
        viewModelScope.launch {
            // Set up ExoPlayer with media item and subtitles
            val mediaItem = MediaItem.Builder()
                .setUri(Uri.fromFile(File(mediaDetails.videoUri)))
                .setSubtitleConfigurations(
                    listOf(
                        MediaItem.SubtitleConfiguration.Builder(Uri.fromFile(mediaDetails.subtitleUri?.let {
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

            if (mediaType == MediaType.Show) {
                val episodes: EpisodeList? =
                    mediaDetails.showId?.let { showRepository.getAllEpisodesForShow(showId = it) }


                val showProgress = mediaDetails.showId?.let { showProgressDao.getShowProgress(it) }
                val mediaItems = episodes?.toMediaItems()
                player.setMediaItems(mediaItems ?: emptyList())

                val index = episodes?.indexOfFirst { it.id == mediaDetails.id }
                if (index != -1) {
                    if (showProgress != null && !startFromBeginning) {
                        if (index != null) {
                            player.seekTo(index, showProgress.playbackPosition)
                        }
                    } else {
                        if (index != null) {
                            player.seekTo(index, 0)
                        }
                    }
                }
            } else {
                val movieProgress = movieProgressDao.getMovieProgress(mediaDetails.id)
                player.setMediaItem(mediaItem)
                if (movieProgress != null && !startFromBeginning) {
                    player.seekTo(movieProgress)
                }
            }
            player.play()
        }
    }

    fun skipToMediaItem(mediaDetails: MediaDetails) {
        viewModelScope.launch {
            val currentMediaItemIndex = player.currentMediaItemIndex

            val episodeList: EpisodeList? =
                mediaDetails.showId?.let { showRepository.getAllEpisodesForShow(showId = it) }
            val currentEpisode = episodeList?.get(currentMediaItemIndex)

            if (currentEpisode != null) {
                val newMediaDetails = MediaDetails(
                    currentEpisode.id,
                    currentEpisode.title,
                    currentEpisode.releaseDate,
                    currentEpisode.videoUri,
                    currentEpisode.subtitleUri,
                    mediaDetails.showId
                )
                _uiState.value = VideoPlayerScreenUiState.Done(newMediaDetails, false)
            }
        }
    }

    fun skipToNextMediaItem(mediaDetails: MediaDetails) {
        player.seekToNextMediaItem()
        skipToMediaItem(mediaDetails)
    }

    fun skipToPreviousMediaItem(mediaDetails: MediaDetails) {
        player.seekToPreviousMediaItem()
        skipToMediaItem(mediaDetails)
    }

    fun List<Episode>.toMediaItems(): List<MediaItem> {
        return this.map { episode ->
            MediaItem.Builder()
                .setUri(Uri.fromFile(File(episode.videoUri)))
                .setSubtitleConfigurations(
                    listOf(
                        MediaItem.SubtitleConfiguration.Builder(Uri.fromFile(episode.subtitleUri.let {
                            File(
                                it
                            )
                        }))
                            .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                            .setLanguage("en")
                            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                            .build()
                    )
                )
                .build()
        }
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

    fun saveCurrentPosition(mediaDetails: MediaDetails, progress: Long, mediaType: MediaType) {
        viewModelScope.launch {
            Log.d("VideoPlayerScreenViewModel", "Saving current position: $progress")
            if (mediaType == MediaType.Movie) {
                movieProgressDao.upsertMovieProgress(MovieProgress(mediaDetails.id, progress))
            }
            if (mediaType == MediaType.Show) {
                mediaDetails.showId?.let { ShowProgress(it, mediaDetails.id, progress) }
                    ?.let { showProgressDao.upsertShowProgress(it) }
            }
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
    data class Done(val mediaDetails: MediaDetails, val startFromBeginning: Boolean) :
        VideoPlayerScreenUiState()
}
