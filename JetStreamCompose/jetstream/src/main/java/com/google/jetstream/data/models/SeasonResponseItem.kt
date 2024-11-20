package com.google.jetstream.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SeasonResponseItem(
    val id: String,
    val seasonNumber: Int,
    val releaseDate: String,
    val episodes: List<EpisodeResponseItem>
)