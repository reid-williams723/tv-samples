package com.google.jetstream.presentation.screens.shows

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.WideCardContainer
import androidx.tv.material3.surfaceColorAtElevation
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.jetstream.R
import com.google.jetstream.data.entities.Episode
import com.google.jetstream.data.entities.Movie
import com.google.jetstream.data.entities.MovieDetails
import com.google.jetstream.data.entities.Show
import com.google.jetstream.data.entities.ShowDetails
import com.google.jetstream.data.util.StringConstants
import com.google.jetstream.presentation.common.Error
import com.google.jetstream.presentation.common.Loading
import com.google.jetstream.presentation.common.MoviesRow
import com.google.jetstream.presentation.screens.dashboard.rememberChildPadding
import com.google.jetstream.presentation.screens.movies.CastAndCrewList
import com.google.jetstream.presentation.screens.movies.MovieDetails
import com.google.jetstream.presentation.screens.movies.MovieDetailsScreenUiState
import com.google.jetstream.presentation.screens.movies.MovieDetailsScreenViewModel
import com.google.jetstream.presentation.screens.movies.MovieReviews
import com.google.jetstream.presentation.screens.movies.TitleValueText
import com.google.jetstream.presentation.theme.JetStreamBottomListPadding
import com.google.jetstream.presentation.theme.JetStreamButtonShape
import kotlinx.coroutines.launch


object ShowDetailsScreen {
    const val ShowIdBundleKey = "showId"
}

val mockEpisodes = listOf(
    Episode(
        id = "1",
        overview = "Episode 1 overview",
        title = "Episode 1",
        seasonNumber = 1,
        episodeNumber = 1,
        releaseDate = "2023",
        duration = "22min",
        stillPath = "https://via.placeholder.com/150" // Replace with a real image URI
    ),
    Episode(
        id = "2",
        overview = "Episode 2 overview",
        title = "Episode 2",
        seasonNumber = 1,
        episodeNumber = 2,
        releaseDate = "2023",
        duration = "22min",
        stillPath = "https://via.placeholder.com/150"
    ),
    Episode(
        id = "3",
        overview = "Episode 3 overview",
        title = "Episode 3",
        seasonNumber = 1,
        episodeNumber = 3,
        releaseDate = "2023",
        duration = "22min",
        stillPath = "https://via.placeholder.com/150"
    ),
    Episode(
        id = "3",
        overview = "Episode 3 overview",
        title = "Episode 3",
        seasonNumber = 1,
        episodeNumber = 3,
        releaseDate = "2023",
        duration = "22min",
        stillPath = "https://via.placeholder.com/150"
    ),
    Episode(
        id = "3",
        overview = "Episode 3 overview",
        title = "Episode 3",
        seasonNumber = 1,
        episodeNumber = 3,
        releaseDate = "2023",
        duration = "22min",
        stillPath = "https://via.placeholder.com/150"
    ),
    Episode(
        id = "3",
        overview = "Episode 3 overview",
        title = "Episode 3",
        seasonNumber = 1,
        episodeNumber = 3,
        releaseDate = "2023",
        duration = "22min",
        stillPath = "https://via.placeholder.com/150"
    ),
    Episode(
        id = "3",
        overview = "Episode 3 overview",
        title = "Episode 3",
        seasonNumber = 1,
        episodeNumber = 3,
        releaseDate = "2023",
        duration = "22min",
        stillPath = "https://via.placeholder.com/150"
    ),
    Episode(
        id = "3",
        overview = "Episode 3 overview",
        title = "Episode 3",
        seasonNumber = 1,
        episodeNumber = 3,
        releaseDate = "2023",
        duration = "22min",
        stillPath = "https://via.placeholder.com/150"
    ),
    Episode(
        id = "3",
        overview = "Episode 3 overview",
        title = "Episode 3",
        seasonNumber = 1,
        episodeNumber = 3,
        releaseDate = "2023",
        duration = "22min",
        stillPath = "https://via.placeholder.com/150"
    ),
    Episode(
        id = "3",
        overview = "Episode 3 overview",
        title = "Episode 3",
        seasonNumber = 1,
        episodeNumber = 3,
        releaseDate = "2023",
        duration = "22min",
        stillPath = "https://via.placeholder.com/150"
    ),
    Episode(
        id = "3",
        overview = "Episode 3 overview",
        title = "Episode 3",
        seasonNumber = 1,
        episodeNumber = 3,
        releaseDate = "2023",
        duration = "22min",
        stillPath = "https://via.placeholder.com/150"
    )
)

val mockShowDetails = Show(
    id = "1",
    title = "Mock Show",
    overview = "A thrilling mock show about previews and details.",
    posterPath = "https://via.placeholder.com/300",
    backdropPath = "https://via.placeholder.com/500",
    genres = listOf("Drama", "Thriller"),
    firstAirDate = "2023",
    lastAirDate = "2023",
    numberOfSeasons = 3,
    numberOfEpisodes = 3,
    episodes = mockEpisodes,
)

@Composable
fun ShowDetailsScreen(
    goToShowPlayer: () -> Unit,
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
            Details2(
                goToShowPlayer = goToShowPlayer,
                showDetails = s.show,
                episodes = s.show.episodes,
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            )
        }
    }
}

@Composable
private fun Details2(
    goToShowPlayer: () -> Unit,
    showDetails: Show = mockShowDetails,
    episodes: List<Episode> = mockEpisodes,
    seasons: List<Int> = listOf(1, 2),
    modifier: Modifier = Modifier,
    currentEpisodeProgress: Long = 0,
) {

    val viewModel = hiltViewModel<ShowDetailsScreenViewModel>()
    val childPadding = rememberChildPadding()
    val focusRequester = remember { FocusRequester() }

    var selectedSeason by remember { mutableStateOf(seasons.firstOrNull() ?: 1) }

//    BackHandler(onBack = onBackPressed)
    Column(
        modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()) // Smooth scrolling for the entire screen
    ) {
        ShowDetails(
            showDetails = showDetails,
            goToShowPlayer = goToShowPlayer
        )

        // Seasons and Episodes item
        SeasonsAndEpisodes2()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Details(
    goToShowPlayer: () -> Unit,
    showDetails: Show = mockShowDetails,
    episodes: List<Episode> = mockEpisodes,
    seasons: List<Int> = listOf(1, 2),
    modifier: Modifier = Modifier,
    currentEpisodeProgress: Long = 0,
) {

    val viewModel = hiltViewModel<ShowDetailsScreenViewModel>()
    val childPadding = rememberChildPadding()
    val focusRequester = remember { FocusRequester() }

    var selectedSeason by remember { mutableStateOf(seasons.firstOrNull() ?: 1) }

//    BackHandler(onBack = onBackPressed)
    LazyColumn(
        contentPadding = PaddingValues(bottom = 135.dp),
        modifier = modifier,
    ) {
        item {
            ShowDetails(
                showDetails = showDetails,
                goToShowPlayer = goToShowPlayer
            )
        }

        // Seasons and Episodes item
        item {
            SeasonsAndEpisodes(
                seasons = seasons,
                episodesBySeason = mapOf(1 to episodes.subList(0, 1), 2 to episodes.subList(1, 2)),
                selectedSeason = 1,
                onSeasonSelected = { seasonNumber ->
                    selectedSeason = seasonNumber
                }
            )
        }


//        item {
//            Text(
//                text = "Episodes",
//                style = MaterialTheme.typography.headlineMedium,
//                modifier = Modifier.padding(start = 16.dp)
//            )
//        }
        // Add episodes directly here
//        items(episodes) { episode ->
//            var isFocused by remember { mutableStateOf(false) }
//            EpisodeCard(
//                episode = episode,
//                modifier = Modifier
//                    .onFocusChanged { focusState ->
//                        isFocused = focusState.isFocused
//                    }
//                    .focusable(), // Make it focusable
//                isFocused = isFocused
//            )
//        }
    }

}

private val BottomDividerPadding = PaddingValues(vertical = 48.dp)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShowDetails(
    showDetails: Show,
    goToShowPlayer: () -> Unit
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
                    WatchShowButton(
                        modifier = Modifier.onFocusChanged {
                            if (it.isFocused) {
                                coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                            }
                        },
                        goToShowPlayer = goToShowPlayer
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
    goToShowPlayer: () -> Unit
) {
    Button(
        onClick = goToShowPlayer,
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

//@Composable
//fun EpisodeRow(episodes: List<Episode>) {
//    Column {
//        episodes.forEach { episode ->
//            EpisodeCard(episode = episode)
//        }
//    }
//}


@Composable
fun EpisodeCard(
    episode: Episode,
    modifier: Modifier = Modifier,
    isFocused: Boolean = false // Handle focus state
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(IntrinsicSize.Min)
            .border(
                width = if (isFocused) 2.dp else 0.dp, // Add border when focused
                color = if (isFocused) Color.White else Color.Transparent,
                shape = RoundedCornerShape(8.dp) // Rounded corners
            )
            .background(if (isFocused) Color.DarkGray else Color.Transparent) // Background change on focus
    ) {
        // Left side: Episode Image (16:9 aspect ratio)
        AsyncImage(
            model = episode.stillPath,
            contentDescription = episode.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(240.dp) // Width for a widescreen aspect ratio
                .aspectRatio(16f / 9f) // Widescreen ratio
                .clip(MaterialTheme.shapes.small) // Rounded corners
        )

        Spacer(modifier = Modifier.width(16.dp)) // Space between image and text

        // Right side: Episode Info
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "S${episode.seasonNumber}E${episode.episodeNumber}: ${episode.title}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = episode.overview,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alpha(0.85f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                // Additional info like release date or runtime
                Text(
                    text = episode.releaseDate,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.alpha(0.75f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = episode.duration, // E.g. "45 min"
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.alpha(0.75f)
                )
            }
        }
    }
}

@Composable
fun SeasonsAndEpisodes(
    seasons: List<Int>,
    episodesBySeason: Map<Int, List<Episode>>,
    selectedSeason: Int,
    onSeasonSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = modifier
            .fillMaxWidth()  // Use fillMaxWidth to constrain width
            .heightIn(max = 600.dp)  // Give a maximum height to avoid infinite constraints
    ) {
        // Seasons Column (on the left)
        LazyColumn(
            modifier = Modifier
                .weight(0.3f)
                .heightIn(max = 600.dp)
                .background(MaterialTheme.colorScheme.background)  // Option 1: Different background color
                .padding(8.dp)  // Padding inside the border
        ) {
            items(seasons) { season ->
                var isFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        }
                        .focusable()
                        .clickable {
                            onSeasonSelected(season)
                        }
                ) {
                    Text(
                        text = "Season ${season}",
                        style = if (isFocused) {
                            MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary)
                        } else {
                            MaterialTheme.typography.titleLarge
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))  // Space between the two columns

        EpisodeColumn(episodes = mockEpisodes, modifier = Modifier.focusRequester(focusRequester))

//        // Episodes Column (on the right)
//        LazyColumn(
//            contentPadding = PaddingValues(bottom = 135.dp),
//            modifier = Modifier
//                .weight(0.7f)
//                .heightIn(max = 600.dp)
//                .background(MaterialTheme.colorScheme.surface)  // Different background color for episodes
//                .padding(8.dp)  // Padding inside the border
//        ) {
//            item {
//                Text(
//                    text = "Episodes - Season $selectedSeason",
//                    style = MaterialTheme.typography.headlineMedium,
//                    modifier = Modifier.padding(start = 16.dp)
//                )
//            }
//
//            // Display episodes for the selected season
//            items(episodesBySeason[selectedSeason] ?: emptyList()) { episode ->
//                var isFocused by remember { mutableStateOf(false) }
//                EpisodeCard(
//                    episode = episode,
//                    modifier = Modifier
//                        .onFocusChanged { focusState ->
//                            isFocused = focusState.isFocused
//                        }
//                        .focusable(),
//                    isFocused = isFocused
//                )
//            }
//        }
    }
}

@Composable
fun SeasonsAndEpisodes2(seasons: List<Int> = listOf(1, 2), episodes: List<Episode> = mockEpisodes) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .heightIn(max = 600.dp)  // Give a maximum height to avoid infinite constraints
    ) {
        LazyColumn(modifier = Modifier.weight(0.3f)) {
            items(seasons) { season ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Handle click event here */ },
//                    shape = MaterialTheme.shapes.medium, // Rounded corners
                    tonalElevation = 4.dp, // Elevation for shadow effect
                ) {
                    Text(text = "Season $season")
                }
            }
        }
        LazyColumn(modifier = Modifier.weight(0.7f)) {
            items(episodes) { episode ->
                EpisodeCard3(episode = episode)
            }
        }
    }
}

@Composable
fun EpisodeColumn(episodes: List<Episode>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = JetStreamBottomListPadding)
    ) {
        item {
            EpisodeCard3(episode = mockEpisodes[0], modifier = modifier)
        }
        item {
            EpisodeCard3(episode = mockEpisodes[1], modifier = modifier)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EpisodeCard3(episode: Episode, modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Set desired height for each surface
            .clickable { /* Handle click event here */ },

        colors = SurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        ),
        shape = MaterialTheme.shapes.extraSmall, // Rounded corners
        tonalElevation = 4.dp, // Elevation for shadow effect
//        color = MaterialTheme.colorScheme.primaryContainer // Background color
    ) {
        WideCardContainer(

            modifier = modifier
                .padding(8.dp),
            title = { Text(text = episode.title) },
            imageCard = {
                AsyncImage(
                    model = episode.stillPath,
                    contentDescription = episode.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(240.dp) // Width for a widescreen aspect ratio
                        .aspectRatio(16f / 9f) // Widescreen ratio
                        .clip(MaterialTheme.shapes.small)
                        .padding(8.dp)// Rounded corners
                )
            },
            subtitle = {
                DotSeparatedRow(
                    texts = listOf(
                        "S${episode.seasonNumber}E${episode.episodeNumber}",
                        episode.duration,
                        episode.releaseDate
                    )
                )
            },
            description = { Text(text = episode.overview) }
        )
    }
}

@Composable
fun EpisodeCard2(episode: Episode, modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Set desired height for each surface
            .clickable { /* Handle click event here */ },
        shape = MaterialTheme.shapes.medium, // Rounded corners
        tonalElevation = 4.dp, // Elevation for shadow effect
//        color = MaterialTheme.colorScheme.primaryContainer // Background color
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Left side: Episode Image (16:9 aspect ratio)
            AsyncImage(
                model = episode.stillPath,
                contentDescription = episode.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(240.dp) // Width for a widescreen aspect ratio
                    .aspectRatio(16f / 9f) // Widescreen ratio
                    .clip(MaterialTheme.shapes.small) // Rounded corners
            )

            Spacer(modifier = Modifier.width(16.dp)) // Space between image and text

            // Right side: Episode Info
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "S${episode.seasonNumber}E${episode.episodeNumber}: ${episode.title}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = episode.overview,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alpha(0.85f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    // Additional info like release date or runtime
                    Text(
                        text = episode.releaseDate,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        modifier = Modifier.alpha(0.75f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = episode.duration, // E.g. "45 min"
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        modifier = Modifier.alpha(0.75f)
                    )
                }
            }
        }
    }
}


@Preview(device = "id:tv_4k")
@Composable
fun PreviewShowDetails() {
    val mockEpisodes = listOf(
        Episode(
            id = "1",
            overview = "Episode description goes here. This could be a few sentences long to describe what happens in the episode.",
            title = "Episode 1",
            seasonNumber = 1,
            episodeNumber = 1,
            duration = "22min",
            releaseDate = "2023",
            stillPath = "https://storage.googleapis.com/androiddevelopers/samples/media/posters/16_9-400/cyber-net.jpg" // Replace with a real image URI
        ),
        Episode(
            id = "2",
            overview = "Episode 2 overview",
            title = "Episode 2",
            seasonNumber = 1,
            episodeNumber = 2,
            duration = "22min",
            releaseDate = "2023",
            stillPath = "https://via.placeholder.com/150"
        ),
        Episode(
            id = "3",
            overview = "Episode 3 overview",
            title = "Episode 3",
            seasonNumber = 1,
            episodeNumber = 3,
            duration = "22min",
            releaseDate = "2023",
            stillPath = "https://via.placeholder.com/150"
        )
    )

    val mockShowDetails = ShowDetails(
        id = "1",
        title = "Mock Show",
        overview = "A thrilling mock show about previews and details.",
        posterPath = "https://via.placeholder.com/300",
        backdropPath = "https://via.placeholder.com/500",
        genres = listOf("Drama", "Thriller"),
        firstAirDate = "2023",
        lastAirDate = "2023",
        numberOfSeasons = 3,
        numberOfEpisodes = 3,
        episodes = mockEpisodes,
    )

    val episode = mockEpisodes[0]
    val isFocused = true


//    ShowDetailsScreen(
//        goToShowPlayer = { /* Handle play action */ },
//        showDetails = mockShowDetails,
//        episodes = mockEpisodes
//    )

    SeasonsAndEpisodes2()

//    EpisodeCard3(episode = episode)

}





