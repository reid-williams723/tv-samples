package com.google.jetstream.presentation.screens.shows

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.jetstream.data.entities.Episode
import com.google.jetstream.data.entities.Show
import com.google.jetstream.data.entities.ShowDetails
import com.google.jetstream.data.repositories.ShowRepository
import com.google.jetstream.data.room.dao.ShowProgressDao
import com.google.jetstream.data.room.entities.ShowProgress
import com.google.jetstream.presentation.screens.videoPlayer.VideoPlayerScreen
import com.google.jetstream.presentation.screens.videoPlayer.VideoPlayerScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ShowDetailsScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    showRepository: ShowRepository,
    val showProgressDao: ShowProgressDao,
) : ViewModel() {

    val uiState = savedStateHandle
        .getStateFlow<String?>(ShowDetailsScreen.ShowIdBundleKey, null)
        .map { showId ->
            if (showId == null) {
                VideoPlayerScreenUiState.Error
            } else {
                val show = showRepository.getShowById(showId)
                val showProgress = showProgressDao.getShowProgress(showId)
                var episode: Episode? = null
                if (showProgress != null) {
                    episode = showRepository.getEpisodeDetailsByShowIdAndEpisodeId(
                        showId,
                        showProgress.episodeId
                    )
                }

                val firstEpisode = showRepository.getShowFirstEpisode(showId)
                Log.d("ShowDetailsScreenViewModel", "show: ${show.id}")
                ShowDetailsScreenUiState.Done(
                    show = show,
                    showProgress = showProgress,
                    firstEpisode = firstEpisode,
                    currentEpisode = episode
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VideoPlayerScreenUiState.Loading
        )
}

sealed class ShowDetailsScreenUiState {
    data object Loading : ShowDetailsScreenUiState()
    data object Error : ShowDetailsScreenUiState()
    data class Done(
        val show: Show,
        val showProgress: ShowProgress?,
        val firstEpisode: Episode? = null,
        val currentEpisode: Episode? = null,
    ) : ShowDetailsScreenUiState()
}