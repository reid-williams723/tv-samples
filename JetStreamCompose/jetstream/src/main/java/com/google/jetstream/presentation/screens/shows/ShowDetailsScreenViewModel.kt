package com.google.jetstream.presentation.screens.shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.jetstream.data.entities.Show
import com.google.jetstream.data.entities.ShowDetails
import com.google.jetstream.data.repositories.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ShowDetailsScreenViewModel @Inject constructor(
    showRepository: ShowRepository
) : ViewModel() {

    val uiState = showRepository.getShows().map {
        ShowDetailsScreenUiState.Done(show = it.get(0))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ShowDetailsScreenUiState.Loading
    )
}

sealed class ShowDetailsScreenUiState {
    data object Loading : ShowDetailsScreenUiState()
    data object Error : ShowDetailsScreenUiState()
    data class Done(val show: Show) : ShowDetailsScreenUiState()
}