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

package com.google.jetstream.presentation.screens.movies

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.jetstream.R
import com.google.jetstream.data.entities.MovieDetails
import com.google.jetstream.data.util.StringConstants
import com.google.jetstream.presentation.screens.dashboard.rememberChildPadding
import com.google.jetstream.presentation.theme.JetStreamButtonShape
import com.google.jetstream.presentation.theme.JetStreamTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieDetails(
    goToMoviePlayerBegin: () -> Unit,
    goToMoviePlayerResume: () -> Unit,
    movieDetails: MovieDetails,
    currentMovieProgress: Long = 0,
) {
    val childPadding = rememberChildPadding()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    val percentageProgress = (currentMovieProgress.toFloat() / (movieDetails.runtimeMins * 60 * 1000))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        MovieImageWithGradients(
            movieDetails = movieDetails,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxWidth(0.55f)
            .align(Alignment.CenterStart)
        ) {
            Spacer(modifier = Modifier.height(108.dp))
            Column(
                modifier = Modifier.padding(start = childPadding.start)
            ) {
                MovieLargeTitle(movieTitle = movieDetails.fullTitle)

                Column(
                    modifier = Modifier.alpha(0.75f)
                ) {
                    MovieDescription(description = movieDetails.plot)
                    DotSeparatedRow(
                        modifier = Modifier.padding(top = 20.dp),
                        texts = listOf(
                            movieDetails.contentRating,
                            movieDetails.releaseDate,
                            movieDetails.genres.joinToString(", "),
                            movieDetails.runtimeStr
                        )
                    )
                    DirectorScreenplayMusicRow(
                        director = movieDetails.directors
                    )
                }
                Row() { // Add padding to the Row
                    if (currentMovieProgress > 0) {
                        ContinueWatchingButton(
                            modifier = Modifier.onFocusChanged {
                                if (it.isFocused) {
                                    coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                                }
                            },
                            goToMoviePlayer = goToMoviePlayerResume,
                            progress = percentageProgress
                        )
                        Spacer(modifier = Modifier.width(16.dp)) // Add horizontal padding
                    }
                    WatchTrailerButton(
                        modifier = Modifier.onFocusChanged {
                            if (it.isFocused) {
                                coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                            }
                        },
                        goToMoviePlayer = goToMoviePlayerBegin
                    )
                }
            }
        }
    }
}

@Composable
private fun ContinueWatchingButton(
    modifier: Modifier = Modifier,
    goToMoviePlayer: () -> Unit,
    progress: Float
) {
    Button(
        onClick = goToMoviePlayer,
        modifier = modifier.padding(top = 24.dp),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        shape = ButtonDefaults.shape(shape = JetStreamButtonShape)
    ) {
//        Box(modifier = Modifier.fillMaxSize()) {
//            // Content inside the button
//            Row(
//                modifier = Modifier
//                    .align(Alignment.Center)
//                    .padding(bottom = 4.dp), // Adjust padding to keep space for progress bar
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.PlayArrow,
//                    contentDescription = null
//                )
//                Spacer(Modifier.size(8.dp))
//                Text(
//                    text = stringResource(R.string.continue_watching),
//                    style = MaterialTheme.typography.titleSmall
//                )
//                ProgressBarIndicator(
//                    progress = 0.5f
//                )
//            }
//
//        }
        Column() {
            Row() {
                Icon(
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.continue_watching),
                    style = MaterialTheme.typography.titleSmall
                )
            }

            ProgressBarIndicator(
                progress = progress
            )
        }


    }
}

@Composable
private fun WatchTrailerButton(
    modifier: Modifier = Modifier,
    goToMoviePlayer: () -> Unit
) {
    Button(
        onClick = goToMoviePlayer,
        modifier = modifier.padding(top = 24.dp),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        shape = ButtonDefaults.shape(shape = JetStreamButtonShape)
    ) {
        Icon(
            imageVector = Icons.Outlined.PlayArrow,
            contentDescription = null
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.watch_now),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun DirectorScreenplayMusicRow(
    director: String
) {
    Row(modifier = Modifier.padding(top = 32.dp)) {
        TitleValueText(
            modifier = Modifier
                .padding(end = 32.dp)
                .weight(1f),
            title = stringResource(R.string.director),
            value = director
        )

//        TitleValueText(
//            modifier = Modifier
//                .padding(end = 32.dp)
//                .weight(1f),
//            title = stringResource(R.string.screenplay),
//            value = screenplay
//        )
//
//        TitleValueText(
//            modifier = Modifier.weight(1f),
//            title = stringResource(R.string.music),
//            value = music
//        )
    }
}

@Composable
private fun MovieDescription(description: String) {
    Text(
        text = description,
        style = MaterialTheme.typography.titleSmall.copy(
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        ),
        modifier = Modifier.padding(top = 8.dp),
        maxLines = 2
    )
}

@Composable
private fun MovieLargeTitle(movieTitle: String) {
    Text(
        text = movieTitle,
        style = MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        maxLines = 1
    )
}

@Composable
private fun MovieImageWithGradients(
    movieDetails: MovieDetails,
    modifier: Modifier = Modifier,
    gradientColor: Color = MaterialTheme.colorScheme.surface,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(movieDetails.posterUri)
            .crossfade(true).build(),
        contentDescription = StringConstants
            .Composable
            .ContentDescription
            .moviePoster(movieDetails.title),
        contentScale = ContentScale.Crop,
        modifier = modifier.drawWithContent {
            drawContent()
            drawRect(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, gradientColor),
                    startY = 600f
                )
            )
            drawRect(
                Brush.horizontalGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    endX = 1000f,
                    startX = 300f
                )
            )
            drawRect(
                Brush.linearGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    start = Offset(x = 500f, y = 500f),
                    end = Offset(x = 1000f, y = 0f)
                )
            )
        }
    )
}

@Composable
fun ProgressBarIndicator(
    progress: Float, // Value between 0.0 and 1.0
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    height: Dp = 4.dp
) {
    Canvas(modifier = modifier.height(height).width(170.dp)) {
        // Draw the background bar
        drawRect(
            color = backgroundColor,
            size = size.copy(width = size.width, height = size.height)
        )

        // Draw the progress bar
        drawRect(
            color = progressColor,
            size = size.copy(width = size.width * progress, height = size.height)
        )
    }
}

