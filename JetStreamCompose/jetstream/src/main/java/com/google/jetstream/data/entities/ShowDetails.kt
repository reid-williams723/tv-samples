package com.google.jetstream.data.entities

data class ShowDetails(
    val id: Int,
    val title: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val genres: List<String>,
    val firstAirDate: String?,
    val lastAirDate: String?,
    val numberOfSeasons: Int,
    val numberOfEpisodes: Int,
    val episodes: List<Episode> // Include the list of episodes
)