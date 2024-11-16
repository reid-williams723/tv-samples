package com.google.jetstream.data.repositories

import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowRepositoryImpl @Inject constructor(
    private val showDataSource: ShowDataSource,
) : ShowRepository {
    override fun getShows() = flow {
        val list = showDataSource.getShowsList()
        emit(list)
    }

    override fun getEpisodesForSeason(showId: String, seasonNumber: Int) = flow {
        val shows = showDataSource.getShowsList()
        val show = shows.find { it.id == showId }
        val season = show?.seasons?.find { it.seasonNumber == seasonNumber }
        season?.episodes?.let { emit(it) }
    }
}