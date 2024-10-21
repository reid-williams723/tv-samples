package com.google.jetstream.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeResponseItem(
    val id: Int,
    val title: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val overview: String?,
    val stillPath: String?
)