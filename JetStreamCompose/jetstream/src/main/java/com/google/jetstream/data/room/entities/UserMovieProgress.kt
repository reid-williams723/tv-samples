package com.google.jetstream.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_movie_progress")
data class UserMovieProgress(
    @PrimaryKey val movieId: String,
    val progress: Long, // Playback position in milliseconds
    val isStarted: Boolean = false
)