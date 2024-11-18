package com.google.jetstream.data.repositories

import com.google.jetstream.storage.DirectoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileReader @Inject constructor(
    private val directoryProvider: DirectoryProvider
) {
    private val directoryPath = directoryProvider.externalDirectoryPath

    suspend fun readJsonData(fileName: String): String = withContext(Dispatchers.IO) {
        val file = File(directoryPath, fileName)
        if (!file.exists()) {
            throw IllegalStateException("File not found: ${file.absolutePath}")
        }
        file.readText() // Reads the file content
    }
}