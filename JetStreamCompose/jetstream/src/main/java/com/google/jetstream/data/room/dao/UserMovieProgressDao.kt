package com.google.jetstream.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.jetstream.data.room.entities.UserMovieProgress

@Dao
interface UserMovieProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: UserMovieProgress)

    @Query("SELECT * FROM user_movie_progress WHERE movieId = :movieId")
    suspend fun getProgressForMovie(movieId: String): UserMovieProgress?
}