package com.google.jetstream.presentation.screens.shows

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.google.jetstream.data.entities.Episode
import com.google.jetstream.data.entities.EpisodeList
import com.google.jetstream.data.entities.Season
import com.google.jetstream.presentation.common.EpisodePosterImage
import com.google.jetstream.presentation.common.ImmersiveListEpisodesRow
import com.google.jetstream.presentation.common.ItemDirection
import com.google.jetstream.presentation.screens.dashboard.rememberChildPadding
import com.google.jetstream.presentation.utils.bringIntoViewIfChildrenAreFocused

@Composable
fun SeasonView(
    season : Season,
    modifier: Modifier = Modifier,
    gradientColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    onEpisodeClick: (episode: Episode) -> Unit
) {
    var isListFocused by remember { mutableStateOf(false) }
    var selectedEpisode by remember(season.episodes) { mutableStateOf(season.episodes.first()) }

    val sectionTitle = if (isListFocused) {
        null
    } else {
       "Season $season.number"
    }

    ImmersiveList(
        selectedEpisode = selectedEpisode,
        isListFocused = isListFocused,
        gradientColor = gradientColor,
        episodeList = season.episodes,
        sectionTitle = sectionTitle,
        onEpisodeClick = onEpisodeClick,
        onEpisodeFocused = {
            selectedEpisode = it
        },
        onFocusChanged = {
            isListFocused = it.hasFocus
        },
        modifier = modifier.bringIntoViewIfChildrenAreFocused(
            PaddingValues(bottom = 116.dp)
        )
    )
}

@Composable
private fun ImmersiveList(
    selectedEpisode: Episode,
    isListFocused: Boolean,
    gradientColor: Color,
    episodeList: EpisodeList,
    sectionTitle: String?,
    onFocusChanged: (FocusState) -> Unit,
    onEpisodeFocused: (Episode) -> Unit,
    onEpisodeClick: (Episode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = modifier
    ) {
        Background(
            episode = selectedEpisode,
            visible = isListFocused,
            modifier = modifier
                .height(432.dp)
                .gradientOverlay(gradientColor)
        )
        Column {
            if (isListFocused) {
                EpisodeDescription(
                    episode = selectedEpisode,
                    modifier = Modifier.padding(
                        start = rememberChildPadding().start,
                        bottom = 40.dp
                    )
                )
            }

            ImmersiveListEpisodesRow(
                episodeList = episodeList,
                itemDirection = ItemDirection.Horizontal,
                title = sectionTitle,
                showItemTitle = !isListFocused,
                showIndexOverImage = true,
                onEpisodeSelected = onEpisodeClick,
                onEpisodeFocused = onEpisodeFocused,
                modifier = Modifier.onFocusChanged(onFocusChanged)
            )
        }
    }
}

@Composable
fun Background(
    episode: Episode,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Crossfade(
            targetState = episode,
            label = "posterUriCrossfade",

            ) {
            EpisodePosterImage(episode = it, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun EpisodeDescription(
    episode: Episode,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = episode.title, style = MaterialTheme.typography.displaySmall)
        Text(
            modifier = Modifier.fillMaxWidth(0.5f),
            text = episode.overview,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            fontWeight = FontWeight.Light
        )
    }
}

private fun Modifier.gradientOverlay(gradientColor: Color): Modifier =
    drawWithCache {
        val horizontalGradient = Brush.horizontalGradient(
            colors = listOf(
                gradientColor,
                Color.Transparent
            ),
            startX = size.width.times(0.2f),
            endX = size.width.times(0.7f)
        )
        val verticalGradient = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                gradientColor
            ),
            endY = size.width.times(0.3f)
        )
        val linearGradient = Brush.linearGradient(
            colors = listOf(
                gradientColor,
                Color.Transparent
            ),
            start = Offset(
                size.width.times(0.2f),
                size.height.times(0.5f)
            ),
            end = Offset(
                size.width.times(0.9f),
                0f
            )
        )

        onDrawWithContent {
            drawContent()
            drawRect(horizontalGradient)
            drawRect(verticalGradient)
            drawRect(linearGradient)
        }
    }
