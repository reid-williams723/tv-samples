package com.google.jetstream.storage

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.jetstream.data.repositories.MovieRepository
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class VolumeInfo(
    val description: String,
    val path: String,
    val state: String = "Unknown"
)

@RequiresApi(Build.VERSION_CODES.R)
@HiltViewModel
class StorageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: MovieRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

//    private val _storageVolumes = MutableStateFlow<List<VolumeInfo>>(emptyList())
//    val storageVolumes: StateFlow<List<VolumeInfo>> get() = _storageVolumes
//
//
//    init {
//        loadStorageVolumes()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.R)
//    private fun loadStorageVolumes() {
//        viewModelScope.launch {
//            val externalDirs = context.getExternalFilesDirs(null)
//            externalDirs.forEach { dir ->
//                dir?.let {
//                    val metadataFile = File(it.absolutePath, "device_metadata.txt")
//                    if (metadataFile.exists()) {
//                        val metadata = metadataFile.readText()
//                        Log.d("StorageViewModel", "Device Metadata: $metadata")
//                    } else {
//                        Log.e("StorageViewModel", "Metadata file not found")
//                    }
//                }
//            }
//
//
//            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
//            val volumes = storageManager.storageVolumes
//
//            val volumeInfoList = volumes.map {
//                VolumeInfo(
//                    description = it.getDescription(context) ?: "Unknown Storage",
//                    path = it.directory?.absolutePath ?: "Unavailable",
//                    state = it.state.toString()
//                )
//            }
//            volumeInfoList.forEach {
//                Log.d("StorageViewModel", "Storage Path: ${it.path}")
//                Log.d("StorageViewModel", "Storage Description: ${it.description}")
//                Log.d("StorageViewModel", "Storage State: ${it.state}")
//            }
//
//            _storageVolumes.value = volumeInfoList
//
//        }
//    }
}
