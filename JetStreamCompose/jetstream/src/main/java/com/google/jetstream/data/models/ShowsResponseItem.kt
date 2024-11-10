package com.google.jetstream.data.models

import com.google.jetstream.data.entities.Season
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
    val seasons: List<SeasonResponseItem>
)