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

package com.google.jetstream.presentation.screens.shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.jetstream.data.entities.MovieList
import com.google.jetstream.data.entities.ShowList
import com.google.jetstream.data.repositories.MovieRepository
import com.google.jetstream.data.repositories.ShowRepository
import com.google.jetstream.presentation.screens.categories.CategoriesScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ShowScreenViewModel @Inject constructor(
    showRepository: ShowRepository
) : ViewModel() {
    val uiState = showRepository.getShows().map {
        ShowScreenUiState.Ready(tvShowList = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ShowScreenUiState.Loading
    )
}

sealed interface ShowScreenUiState {
    data object Loading : ShowScreenUiState
    data class Ready(
        val tvShowList: ShowList
    ) : ShowScreenUiState
}
