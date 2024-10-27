package com.google.jetstream.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ShowsResponseItem(
    val id: String,
    val title: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String,
    val genres: List<String>,
    val firstAirDate: String,
    val lastAirDate: String,
    val numberOfSeasons: Int,
    val numberOfEpisodes: Int,
    val episodes: List<EpisodeResponseItem> // Include the list of episodes
)