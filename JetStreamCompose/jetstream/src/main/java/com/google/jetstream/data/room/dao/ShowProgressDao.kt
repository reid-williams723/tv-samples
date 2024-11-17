package com.google.jetstream.data.room.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.jetstream.data.room.entities.ShowProgress

@Dao
interface ShowProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertShowProgress(showProgress: ShowProgress)

    @Query("SELECT showId, episodeId, playbackPosition FROM show_progress WHERE showId = :showId")
    suspend fun getShowProgress(showId: String): ShowProgress?
}