package com.google.jetstream.data.repositories

import com.google.jetstream.data.entities.Episode
import com.google.jetstream.data.entities.EpisodeDetails
import com.google.jetstream.data.entities.EpisodeList
import com.google.jetstream.data.entities.Show
import com.google.jetstream.data.entities.ShowList
import kotlinx.coroutines.flow.Flow

interface ShowRepository {
    fun getShows(): Flow<ShowList>
    suspend fun getShowById(showId: String): Show
    fun getEpisodesForSeason(showId: String, seasonNumber: Int): Flow<EpisodeList>
    fun getAllEpisodesForShow(showId: String): Flow<EpisodeList>
    suspend fun getEpisodeDetailsByShowIdAndEpisodeId(showId: String, episodeId: String): Episode
    suspend fun getShowFirstEpisode(showId: String): Episode
}