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
}