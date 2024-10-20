package com.google.jetstream.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_progress")
data class MovieProgress(
    @PrimaryKey val movieId: String,
    val playbackPosition: Long, // Playback position in milliseconds
)