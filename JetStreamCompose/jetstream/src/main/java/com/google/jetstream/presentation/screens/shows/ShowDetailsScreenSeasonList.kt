package com.google.jetstream.presentation.screens.shows

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.CompactCard
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.google.jetstream.data.entities.Episode
import com.google.jetstream.data.entities.Season
import com.google.jetstream.data.util.StringConstants
import com.google.jetstream.presentation.screens.dashboard.rememberChildPadding
import com.google.jetstream.presentation.theme.JetStreamBorderWidth

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowDetailsScreenSeasonList(
    modifier: Modifier = Modifier,
    season: Season,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    onEpisodeClick: (episode: Episode) -> Unit,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    )
) {

    val (lazyRow, firstItem) = remember { FocusRequester.createRefs() }

    Column(
        modifier = modifier.focusGroup()
    ) {
        Text(
            text = "Season ${season.seasonNumber}",
            style = titleStyle,
            modifier = Modifier
                .alpha(1f)
                .padding(start = startPadding, top = 16.dp, bottom = 16.dp)
        )
        AnimatedContent(
            modifier = modifier,
            targetState = season.episodes,
            label = "",
        ) { episodeListTarget ->
            // ToDo: specify the pivot offset to 0.07f
            LazyRow(
                modifier = Modifier
                    .focusRequester(lazyRow)
                    .focusRestorer {
                        firstItem
                    },
                contentPadding = PaddingValues(start = startPadding, end = endPadding)
            ) {
                itemsIndexed(
                    episodeListTarget,
                    key = { _, episode ->
                        episode.id
                    }
                ) { index, episode ->
                    val itemModifier = if (index == 0) {
                        Modifier.focusRequester(firstItem)
                    } else {
                        Modifier
                    }

                    EpisodeListItem(
                        modifier = itemModifier,
                        itemWidth = 432.dp,
                        onEpisodeClick = onEpisodeClick,
                        episode = episode,
                    )
                }
            }
        }
    }
}

@Composable
private fun EpisodeListItem(
    itemWidth: Dp,
    episode: Episode,
    modifier: Modifier = Modifier,
    onEpisodeClick: (episode: Episode) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(JetStreamBorderWidth))
        var isFocused by remember { mutableStateOf(false) }
        CompactCard(
            modifier = modifier
                .width(itemWidth)
                .aspectRatio(2f)
                .padding(end = 32.dp)
                .onFocusChanged { isFocused = it.isFocused || it.hasFocus },
            scale = CardDefaults.scale(focusedScale = 1f),
            border = CardDefaults.border(
                focusedBorder = Border(
                    border = BorderStroke(
                        width = JetStreamBorderWidth, color = MaterialTheme.colorScheme.onSurface
                    )
                )
            ),
            colors = CardDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            onClick = { onEpisodeClick(episode) },
            image = {
                val contentAlpha by animateFloatAsState(
                    targetValue = if (isFocused) 1f else 0.5f,
                    label = "",
                )
                AsyncImage(
                    model = episode.stillPath,
                    contentDescription = StringConstants
                        .Composable
                        .ContentDescription
                        .moviePoster(episode.title),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = contentAlpha }
                )
            },
            title = {
                Column {
                    Text(
                        text = episode.overview,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier
                            .graphicsLayer { alpha = 0.6f }
                            .padding(start = 24.dp)
                    )
                    Text(
                        text = episode.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 24.dp
                        ),
                        // TODO: Remove this when CardContent is not overriding contentColor anymore
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}