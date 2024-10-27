package com.google.jetstream.data.entities

data class EpisodeDetails(
    val id: String,
    val title: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val overview: String,
    val stillPath: String,
    val releaseDate: String,
    val duration: String
) {
}