package com.google.jetstream.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.google.jetstream.data.room.dao.UserMovieProgressDao
import com.google.jetstream.data.room.entities.UserMovieProgress

@Database(entities = [UserMovieProgress::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userMovieProgressDao(): UserMovieProgressDao
}