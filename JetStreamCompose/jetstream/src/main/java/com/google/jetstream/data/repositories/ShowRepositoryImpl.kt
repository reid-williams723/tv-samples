package com.google.jetstream.data.repositories

import com.google.jetstream.data.entities.Episode
import com.google.jetstream.data.entities.EpisodeDetails
import com.google.jetstream.data.entities.EpisodeList
import com.google.jetstream.data.entities.Show
import kotlinx.coroutines.flow.Flow
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

    override suspend fun getShowById(showId: String): Show {
        val shows = showDataSource.getShowsList()
        return shows.find { it.id == showId } ?: shows.first()
    }

    override fun getEpisodesForSeason(showId: String, seasonNumber: Int) = flow {
        val shows = showDataSource.getShowsList()
        val show = shows.find { it.id == showId }
        val season = show?.seasons?.find { it.seasonNumber == seasonNumber }
        season?.episodes?.let { emit(it) }
    }

    override suspend fun getAllEpisodesForShow(showId: String): EpisodeList {
        val shows = showDataSource.getShowsList()
        val show = shows.find { it.id == showId }
        return show?.seasons?.flatMap { it.episodes } ?: emptyList()
    }

    override suspend fun getEpisodeDetailsByShowIdAndEpisodeId(
        showId: String,
        episodeId: String
    ): Episode {
        val shows = showDataSource.getShowsList()
        val show = shows.find { it.id == showId }
        val episode = show?.seasons?.flatMap { it.episodes }?.find { it.id == episodeId }
        return episode ?: throw Exception("Episode not found")
    }

    override suspend fun getShowFirstEpisode(showId: String): Episode {
        val shows = showDataSource.getShowsList()
        val show = shows.find { it.id == showId }
        val firstSeason = show?.seasons?.first()
        val firstEpisode = firstSeason?.episodes?.first()
        return firstEpisode ?: throw Exception("Episode not found")
    }
}