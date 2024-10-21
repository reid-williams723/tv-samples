package com.google.jetstream.data.entities

data class EpisodeDetails(
    val id: Int,
    val title: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val overview: String?,
    val stillPath: String?
) {
}