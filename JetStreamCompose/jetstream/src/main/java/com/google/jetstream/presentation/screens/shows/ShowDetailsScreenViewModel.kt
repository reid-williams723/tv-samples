package com.google.jetstream.presentation.screens.shows

import androidx.lifecycle.ViewModel
import com.google.jetstream.data.entities.ShowDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShowDetailsScreenViewModel @Inject constructor(
) : ViewModel() {

}

sealed class ShowDetailsScreenUiState {
    data object Loading : ShowDetailsScreenUiState()
    data object Error : ShowDetailsScreenUiState()
    data class Done(val showDetails: ShowDetails) : ShowDetailsScreenUiState()
}