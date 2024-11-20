package com.google.jetstream.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.jetstream.data.room.entities.MovieProgress

@Dao
interface MovieProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovieProgress(movieProgress: MovieProgress)

    @Query("SELECT playbackPosition FROM movie_progress WHERE movieId = :movieId")
    suspend fun getMovieProgress(movieId: String): Long?
}