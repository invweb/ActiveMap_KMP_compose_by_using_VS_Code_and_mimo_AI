package com.activemap.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.activemap.shared.repository.InMemoryLocationRepository
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.desktop.ui.ActiveMapDesktopApp

fun main() = application {
    val windowState = rememberWindowState(width = 1200.dp, height = 800.dp)
    
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Active Map"
    ) {
        val repository = remember { InMemoryLocationRepository() }
        val viewModel = remember { LocationViewModel(repository) }
        
        MaterialTheme {
            ActiveMapDesktopApp(viewModel = viewModel)
        }
    }
}
