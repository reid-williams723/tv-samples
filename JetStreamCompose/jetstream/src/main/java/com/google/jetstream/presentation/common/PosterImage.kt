/*
 * Copyright 2024 Google LLC
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

package com.google.jetstream.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.jetstream.data.entities.Episode
import com.google.jetstream.data.entities.Movie
import com.google.jetstream.data.entities.Show
import com.google.jetstream.data.util.StringConstants
import java.io.File

@Composable
fun PosterImage(
    movie: Movie,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .crossfade(true)
            .data(File(movie.posterUri))
            .build(),
        contentDescription = StringConstants.Composable.ContentDescription.moviePoster(movie.title),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun EpisodePosterImage(
    episode: Episode,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .crossfade(true)
            .data(episode.stillPath)
            .build(),
        contentDescription = StringConstants.Composable.ContentDescription.moviePoster(episode.title),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ShowPosterImage(
    show: Show,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .crossfade(true)
            .data(show.posterPath)
            .build(),
        contentDescription = StringConstants.Composable.ContentDescription.moviePoster(show.title),
        contentScale = ContentScale.Crop
    )
}
