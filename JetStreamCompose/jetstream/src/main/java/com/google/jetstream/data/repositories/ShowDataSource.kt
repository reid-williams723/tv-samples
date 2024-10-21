package com.google.jetstream.data.repositories

import com.google.jetstream.data.entities.toShow
import com.google.jetstream.data.util.AssetsReader
import com.google.jetstream.data.util.StringConstants
import javax.inject.Inject

class ShowDataSource @Inject constructor(
    assetsReader: AssetsReader
) {

    private val showsDataReader = CachedDataReader {
        readShowData(assetsReader, StringConstants.Assets.Shows)
    }

    suspend fun getShowsList() =
        showsDataReader.read().map {
            it.toShow()
        }
}