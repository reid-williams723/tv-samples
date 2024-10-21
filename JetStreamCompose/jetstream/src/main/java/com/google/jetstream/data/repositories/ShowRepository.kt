package com.google.jetstream.data.repositories

import com.google.jetstream.data.entities.ShowList
import kotlinx.coroutines.flow.Flow

interface ShowRepository {
    fun getShows(): Flow<ShowList>
}