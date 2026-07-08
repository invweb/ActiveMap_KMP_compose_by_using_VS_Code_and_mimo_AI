package com.activemap.web

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import com.activemap.shared.repository.InMemoryLocationRepository
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.web.ui.ActiveMapWebApp

fun main() {
    renderComposable(rootElementId = "root") {
        val repository = remember { InMemoryLocationRepository() }
        val viewModel = remember { LocationViewModel(repository) }
        
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
