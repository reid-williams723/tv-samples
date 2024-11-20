package com.google.jetstream.data.entities

import com.google.jetstream.data.models.SeasonResponseItem

data class Season(
    val id: String,
    val seasonNumber: Int,
    val releaseDate: String,
    val episodes: List<Episode>
)

fun SeasonResponseItem.toSeason(): Season {
    val episodes = episodes.map { it.toEpisode() }
    return Season(id, seasonNumber, releaseDate, episodes)
}