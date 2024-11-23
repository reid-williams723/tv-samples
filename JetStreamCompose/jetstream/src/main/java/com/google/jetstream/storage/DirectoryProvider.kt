package com.google.jetstream.storage

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.google.jetstream.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DirectoryProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
//    val externalDirectoryPath: String by lazy {
//        // Logic to determine the correct external directory
//        context.getExternalFilesDirs(null).firstOrNull()?.absolutePath
//            ?: throw IllegalStateException("No external directory found")
//    }

    val externalDirectoryPath: String by lazy {
        val externalDirs = context.getExternalFilesDirs(null)
        var externalPath: String? = null
        externalDirs.forEach { dir ->
            val metadataFile = File(dir.absolutePath, "device_metadata.txt")
            if (metadataFile.exists()) {
                try {
                    val metadata = metadataFile.readText()
                    Log.d("DirectoryProvider", "Device Metadata: $metadata")

                    // If metadata matches your criteria, return the directory path
                    if (isValidMetadata(metadata)) {
                        externalPath = dir.absolutePath
                        return@forEach
                    } else {
                        // Handle invalid metadata (e.g., log an error)
                        Log.e("DirectoryProvider", "Invalid metadata found in ${dir.absolutePath}")
                    }
                } catch (e: Exception) {
                    Log.e("DirectoryProvider", "Error reading metadata file", e)
                }
            } else {
                Log.e("DirectoryProvider", "Metadata file not found in ${dir.absolutePath}")
            }
        }
        externalPath ?: throw IllegalStateException("No valid external directory found")
    }

    // Example method to validate metadata (you can customize this logic)
    private fun isValidMetadata(metadata: String): Boolean {
        // Logic to determine if the metadata is valid
        return metadata.contains("DeviceID=12345") // Just an example
    }
}
