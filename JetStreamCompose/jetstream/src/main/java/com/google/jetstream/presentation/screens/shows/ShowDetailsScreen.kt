package com.google.jetstream.presentation.screens.shows

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.jetstream.data.entities.Movie

@Composable
fun ShowsDetailScreen(
    goToMoviePlayer: () -> Unit,
    onBackPressed: () -> Unit,
    refreshScreenWithNewMovie: (Movie) -> Unit,
    showDetailsScreenViewModel: ShowDetailsScreenViewModel = hiltViewModel()
) {
}