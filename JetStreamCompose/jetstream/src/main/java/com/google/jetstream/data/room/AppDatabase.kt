package com.google.jetstream.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.google.jetstream.data.room.dao.MovieProgressDao
import com.google.jetstream.data.room.dao.ShowProgressDao
import com.google.jetstream.data.room.entities.MovieProgress
import com.google.jetstream.data.room.entities.ShowProgress

@Database(entities = [MovieProgress::class, ShowProgress::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieProgressDao(): MovieProgressDao
    abstract fun showProgressDao(): ShowProgressDao
}