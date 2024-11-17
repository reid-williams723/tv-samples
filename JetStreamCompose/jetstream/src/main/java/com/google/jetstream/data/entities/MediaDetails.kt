package com.google.jetstream.data.entities

data class MediaDetails(
    val id: String,
    val name: String,
    val releaseDate: String,
    val videoUri: String,
    val subtitleUri: String?,
    val showId: String?
)