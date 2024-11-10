package com.google.jetstream.data.entities

import com.google.jetstream.data.models.ShowsResponseItem

data class Show(
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
    val seasons: List<Season>
)

fun ShowsResponseItem.toShow(): Show {
    val seasons = seasons.map { it.toSeason() }
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
        seasons
    )
}
