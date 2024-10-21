package com.google.jetstream.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.google.jetstream.data.room.dao.MovieProgressDao
import com.google.jetstream.data.room.entities.MovieProgress

@Database(entities = [MovieProgress::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieProgressDao(): MovieProgressDao
}