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

package com.google.jetstream.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.jetstream.data.entities.MovieList
import com.google.jetstream.data.entities.ShowList
import com.google.jetstream.data.repositories.MovieRepository
import com.google.jetstream.data.repositories.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeScreeViewModel @Inject constructor(
    movieRepository: MovieRepository,
    showRepository: ShowRepository
) : ViewModel() {

    val uiState: StateFlow<HomeScreenUiState> = combine(
        movieRepository.getMovies(),
        showRepository.getShows()
    ) { movieList, showList ->
        HomeScreenUiState.Ready(
            movieList,
            showList,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenUiState.Loading
    )
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data object Error : HomeScreenUiState
    data class Ready(
        val movieList: MovieList,
        val showList: ShowList,
    ) : HomeScreenUiState
}
