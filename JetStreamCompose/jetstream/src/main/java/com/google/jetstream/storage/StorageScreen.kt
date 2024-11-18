package com.google.jetstream.storage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

//@RequiresApi(Build.VERSION_CODES.R)
//@Composable
//fun StorageScreen(viewModel: StorageViewModel = hiltViewModel()) {
//    val volumes by viewModel.storageVolumes.collectAsState()
//
//    LazyColumn {
//        items(volumes) { volume ->
//            Text(
//                text = "${volume.description}: ${volume.path}",
//                modifier = Modifier
//                    .padding(16.dp)
//                    .fillMaxWidth(),
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }
//    }
//}

