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

package com.google.jetstream.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.jetstream.data.enum.MediaType
import com.google.jetstream.presentation.screens.Screens
import com.google.jetstream.presentation.screens.categories.CategoryMovieListScreen
import com.google.jetstream.presentation.screens.dashboard.DashboardScreen
import com.google.jetstream.presentation.screens.movies.MovieDetailsScreen
import com.google.jetstream.presentation.screens.shows.ShowDetailsScreen
import com.google.jetstream.presentation.screens.videoPlayer.VideoPlayerScreen

@Composable
fun App(
    onBackPressed: () -> Unit
) {

    val navController = rememberNavController()
    var isComingBackFromDifferentScreen by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Screens.Dashboard(),
        builder = {
            composable(
                route = Screens.CategoryMovieList(),
                arguments = listOf(
                    navArgument(CategoryMovieListScreen.CategoryIdBundleKey) {
                        type = NavType.StringType
                    }
                )
            ) {
                CategoryMovieListScreen(
                    onBackPressed = {
                        if (navController.navigateUp()) {
                            isComingBackFromDifferentScreen = true
                        }
                    },
                    onMovieSelected = { movie ->
                        navController.navigate(
                            Screens.MovieDetails.withArgs(movie.id)
                        )
                    }
                )
            }
            composable(
                route = Screens.MovieDetails(),
                arguments = listOf(
                    navArgument(MovieDetailsScreen.MovieIdBundleKey) {
                        type = NavType.StringType
                    }
                )
            ) {
                MovieDetailsScreen(
                    goToMoviePlayerBegin = { movieId, startFromBeginning, mediaType, showId ->
                        val showIdAny: Any = showId ?: ""
                        navController.navigate(
                            Screens.VideoPlayer.withArgs(
                                movieId,
                                startFromBeginning,
                                mediaType,
                                showIdAny
                            )
                        )
                    },
                    goToMoviePlayerResume = { movieId, startFromBeginning, mediaType, showId ->
                        val showIdAny: Any = showId ?: ""
                        navController.navigate(
                            Screens.VideoPlayer.withArgs(
                                movieId,
                                startFromBeginning,
                                mediaType,
                                showIdAny
                            )
                        )
                    },
                    refreshScreenWithNewMovie = { movie ->
                        navController.navigate(
                            Screens.MovieDetails.withArgs(movie.id)
                        ) {
                            popUpTo(Screens.MovieDetails()) {
                                inclusive = true
                            }
                        }
                    },
                    onBackPressed = {
                        if (navController.navigateUp()) {
                            isComingBackFromDifferentScreen = true
                        }
                    }
                )
            }
            composable(
                route = Screens.ShowDetails(),
                arguments = listOf(
                    navArgument(ShowDetailsScreen.ShowIdBundleKey) {
                        type = NavType.StringType
                    }
                )
            ) {
                ShowDetailsScreen(
                    goToEpisodePlayer = { episodeId, startFromBeginning, mediaType, showId ->
                        navController.navigate(
                            Screens.VideoPlayer.withArgs(
                                episodeId,
                                startFromBeginning,
                                mediaType,
                                showId
                            )
                        )
                    },
                    goToShowPlayerBegin = { episodeId, startFromBeginning, mediaType, showId ->
                        navController.navigate(
                            Screens.VideoPlayer.withArgs(
                                episodeId,
                                startFromBeginning,
                                mediaType,
                                showId
                            )
                        )
                    },
                    goToShowPlayerResume = { episodeId, startFromBeginning, mediaType, showId ->
                        navController.navigate(
                            Screens.VideoPlayer.withArgs(
                                episodeId,
                                startFromBeginning,
                                mediaType,
                                showId
                            )
                        )
                    },
                )
            }
            composable(route = Screens.Dashboard()) {
                DashboardScreen(
                    openCategoryMovieList = { categoryId ->
                        navController.navigate(
                            Screens.CategoryMovieList.withArgs(categoryId)
                        )
                    },
                    openMovieDetailsScreen = { movieId ->
                        navController.navigate(
                            Screens.MovieDetails.withArgs(movieId)
                        )
                    },
                    openShowDetailsScreen = { showId ->
                        navController.navigate(
                            Screens.ShowDetails.withArgs(showId)
                        )
                    },
                    openVideoPlayer = {
                        navController.navigate(Screens.VideoPlayer())
                    },
                    onBackPressed = onBackPressed,
                    isComingBackFromDifferentScreen = isComingBackFromDifferentScreen,
                    resetIsComingBackFromDifferentScreen = {
                        isComingBackFromDifferentScreen = false
                    }
                )
            }
            composable(route = Screens.VideoPlayer(),
                arguments = listOf(
                    navArgument(VideoPlayerScreen.MovieIdBundleKey) {
                        type = NavType.StringType
                    },
                    navArgument(VideoPlayerScreen.StartFromBeginningKey) {
                        type = NavType.BoolType
                    },
                    navArgument(VideoPlayerScreen.MediaTypeBundleKey) {
                        type = NavType.StringType
                    },
                    navArgument(VideoPlayerScreen.ShowIdBundleKey) {
                        type = NavType.StringType
                    }
                )
            ) {
                val movieId = it.arguments?.getString(VideoPlayerScreen.MovieIdBundleKey)
                val startFromBeginning =
                    it.arguments?.getBoolean(VideoPlayerScreen.StartFromBeginningKey)
                var mediaType = it.arguments?.getString(VideoPlayerScreen.MediaTypeBundleKey)
                val showId = it.arguments?.getString(VideoPlayerScreen.ShowIdBundleKey)

                // Default to movie if no media type is provided
                if (mediaType == null) {
                    mediaType = MediaType.Movie.name
                }

                if (movieId == null || startFromBeginning == null) {
                    return@composable
                }

                VideoPlayerScreen(
                    showId = showId,
                    mediaType = MediaType.valueOf(mediaType),
                    movieId = movieId,
                    startFromBeginning = startFromBeginning,
                    onBackPressed = {
                        if (navController.navigateUp()) {
                            isComingBackFromDifferentScreen = true
                        }
                    }
                )
            }
        }
    )
}
