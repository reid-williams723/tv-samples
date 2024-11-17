package com.google.jetstream.data.entities

import com.google.jetstream.data.models.EpisodeResponseItem

data class Episode(
    val id: String,
    val title: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val overview: String,
    val stillPath: String,
    val releaseDate: String,
    val duration: String,
    val videoUri: String,
    val subtitleUri: String
)

fun EpisodeResponseItem.toEpisode(): Episode {
    return Episode(
        id,
        title,
        seasonNumber,
        episodeNumber,
        overview,
        stillPath,
        releaseDate,
        duration,
        videoUri,
        subtitleUri
    )
}