package com.activemap.web

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.web.ui.ActiveMapWebApp
import com.activemap.shared.di.appModule
import com.activemap.web.di.webModule
import org.koin.compose.koinInject
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(appModule, webModule)
    }
    
    renderComposable(rootElementId = "root") {
        val viewModel: LocationViewModel = koinInject()
        
        DisposableEffect(Unit) {
            onDispose {
                viewModel.close()
            }
        }
        
        Style {
            body {
                margin(0.px)
                padding(0.px)
                fontFamily("Arial, sans-serif")
            }
        }
        
        ActiveMapWebApp(viewModel = viewModel)
    }
}