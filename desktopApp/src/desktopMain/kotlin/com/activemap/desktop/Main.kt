package com.activemap.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.desktop.ui.ActiveMapDesktopApp
import com.activemap.shared.di.appModule
import com.activemap.desktop.di.desktopModule
import org.koin.compose.koinInject
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(appModule, desktopModule)
    }
    
    val windowState = rememberWindowState(width = 1200.dp, height = 800.dp)
    
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Active Map"
    ) {
        val viewModel: LocationViewModel = koinInject()
        
        DisposableEffect(Unit) {
            onDispose {
                viewModel.close()
            }
        }
        
        MaterialTheme {
            ActiveMapDesktopApp(viewModel = viewModel)
        }
    }
}