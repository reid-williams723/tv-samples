package com.google.jetstream.presentation.screens.shows

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.jetstream.R
import com.google.jetstream.data.entities.Episode
import com.google.jetstream.data.entities.Show
import com.google.jetstream.data.enum.MediaType
import com.google.jetstream.presentation.common.Error
import com.google.jetstream.presentation.common.Loading
import com.google.jetstream.presentation.screens.dashboard.rememberChildPadding
import com.google.jetstream.presentation.theme.JetStreamButtonShape
import kotlinx.coroutines.launch


object ShowDetailsScreen {
    const val ShowIdBundleKey = "showId"
}

@Composable
fun ShowDetailsScreen(
    goToEpisodePlayer: (String ,Boolean, MediaType, String) -> Unit,
    goToShowPlayerBegin: (String, Boolean, MediaType, String) -> Unit,
    goToShowPlayerResume: (String, Boolean, MediaType, String) -> Unit,
    showDetailsScreenViewModel: ShowDetailsScreenViewModel = hiltViewModel()
) {
    val uiState by showDetailsScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is ShowDetailsScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is ShowDetailsScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }
        is ShowDetailsScreenUiState.Done -> {
            Details(
                goToEpisodePlayer = goToEpisodePlayer,
                goToShowPlayerResume = {
                    s.showProgress?.let {
                        goToShowPlayerResume(
                            it.episodeId,
                            false,
                            MediaType.Show,
                            s.show.id
                        )
                    }
                },
                goToShowPlayerBegin = {
                    s.showProgress?.let {
                        s.firstEpisode?.let { it1 ->
                            goToShowPlayerBegin(
                                it1.id,
                                true,
                                MediaType.Show,
                                s.show.id
                            )
                        }
                    }
                },
                showDetails = s.show,
                currentEpisode = s.currentEpisode,
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            )
        }
    }
}

@Composable
private fun Details(
    goToEpisodePlayer: (String ,Boolean, MediaType, String) -> Unit,
    goToShowPlayerBegin: () -> Unit,
    goToShowPlayerResume: () -> Unit,
    showDetails: Show,
    modifier: Modifier = Modifier,
    currentEpisodeProgress: Long = 0,
    currentEpisode: Episode? = null
) {

    val viewModel = hiltViewModel<ShowDetailsScreenViewModel>()
    val childPadding = rememberChildPadding()
    val focusRequester = remember { FocusRequester() }

    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 108.dp),
        // Setting overscan margin to bottom to ensure the last row's visibility
        modifier = modifier
    ) {
        item {
            ShowDetails(
                showDetails = showDetails,
                goToShowPlayerBegin = goToShowPlayerBegin,
                goToShowPlayerResume = goToShowPlayerResume,
                currentEpisode = currentEpisode
            )
        }
        items(showDetails.seasons) { season ->
            ShowDetailsScreenSeasonList(showId = showDetails.id, season = season, onEpisodeClick = goToEpisodePlayer)
        }
    }
}

private val BottomDividerPadding = PaddingValues(vertical = 48.dp)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShowDetails(
    showDetails: Show,
    goToShowPlayerBegin: () -> Unit,
    goToShowPlayerResume: () -> Unit,
    currentEpisode: Episode? = null
) {
    val childPadding = rememberChildPadding()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(432.dp)
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        ShowImageWithGradients(
            showDetails = showDetails,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxWidth(0.55f)) {
            Spacer(modifier = Modifier.height(108.dp))
            Column(
                modifier = Modifier.padding(start = childPadding.start)
            ) {
                ShowLargeTitle(showTitle = showDetails.title)

                Column(
                    modifier = Modifier.alpha(0.75f)
                ) {
                    ShowDescription(description = showDetails.overview)
                    DotSeparatedRow(
                        modifier = Modifier.padding(top = 20.dp),
                        texts = listOf(
                            showDetails.firstAirDate,
                            showDetails.genres.joinToString(", "),
                            "${showDetails.numberOfSeasons} Seasons"
                        )
                    )
                }

                // Row for Play button
                Row {
                    if (currentEpisode != null) {
                        ContinueWatchingButton(
                            modifier = Modifier.onFocusChanged {
                                if (it.isFocused) {
                                    coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                                }
                            },
                            goToShowPlayerResume = goToShowPlayerResume,
                            currentEpisode = currentEpisode
                        )
                        Spacer(modifier = Modifier.width(16.dp)) // Add horizontal padding
                    }

                    WatchShowButton(
                        modifier = Modifier.onFocusChanged {
                            if (it.isFocused) {
                                coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                            }
                        },
                        goToShowPlayerBegin = goToShowPlayerBegin
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowImageWithGradients(
    showDetails: Show,
    modifier: Modifier = Modifier,
    gradientColor: Color = MaterialTheme.colorScheme.surface,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(showDetails.backdropPath)
            .crossfade(true).build(),
        contentDescription = showDetails.title,
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
        }
    )
}

@Composable
private fun ShowLargeTitle(showTitle: String) {
    Text(
        text = showTitle,
        style = MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        maxLines = 1
    )
}

@Composable
private fun ShowDescription(description: String) {
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
private fun DotSeparatedRow(
    modifier: Modifier = Modifier,
    texts: List<String>
) {
    Row(modifier = modifier) {
        texts.forEachIndexed { index, text ->
            Text(text = text)
            if (index != texts.size - 1) {
                Text(" • ")
            }
        }
    }
}

@Composable
private fun WatchShowButton(
    modifier: Modifier = Modifier,
    goToShowPlayerBegin: () -> Unit
) {
    Button(
        onClick = goToShowPlayerBegin,
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
private fun ContinueWatchingButton(
    modifier: Modifier = Modifier,
    goToShowPlayerResume: () -> Unit,
    currentEpisode: Episode
) {
    Button(
        onClick = goToShowPlayerResume,
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
            text = "Continue Watching S${currentEpisode.seasonNumber}E${currentEpisode.episodeNumber}",
            style = MaterialTheme.typography.titleSmall
        )
    }
}










