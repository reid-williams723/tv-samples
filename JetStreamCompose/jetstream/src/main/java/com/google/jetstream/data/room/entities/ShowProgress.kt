package com.google.jetstream.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "show_progress")
data class ShowProgress(
    @PrimaryKey val showId: String,
    val episodeId: String,
    val playbackPosition: Long,
)