package com.google.jetstream.data.entities

import com.google.jetstream.data.models.ShowsResponseItem

data class Show(
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

fun ShowsResponseItem.toShow(): Show {
    val episodes = episodes.map { it.toEpisode() }
    return Show(
        id,
        title,
        overview,
        posterPath,
        backdropPath,
        genres,
        firstAirDate,
        lastAirDate,
        numberOfSeasons,
        numberOfEpisodes,
        episodes
    )
}
