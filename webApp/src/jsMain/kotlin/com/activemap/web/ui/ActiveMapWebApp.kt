package com.activemap.web.ui

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.shared.model.Location
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.web.ui.components.LocationListWeb
import com.activemap.web.ui.components.LocationDetailWeb
import com.activemap.web.ui.components.AddLocationFormWeb
import com.activemap.web.ui.components.MapViewWeb

@Composable
fun ActiveMapWebApp(viewModel: LocationViewModel) {
    val locations by viewModel.locations.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val isAddingLocation by viewModel.isAddingLocation.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    
    var currentScreen by remember { mutableStateOf(Screen.MAP) }
    
    Div(
        attrs = {
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                height(100.vh)
                width(100.vw)
            }
        }
    ) {
        // Top navigation
        Nav(
            attrs = {
                style {
                    display(DisplayStyle.Flex)
                    justifyContent(JustifyContent.SpaceBetween)
                    alignItems(AlignItems.Center)
                    padding(16.px)
                    backgroundColor(Color("#1976d2"))
                    color(Color.White)
                }
            }
        ) {
            H1(
                attrs = {
                    style {
                        margin(0.px)
                        fontSize(24.px)
                    }
                }
            ) {
                Text("Active Map")
            }
            
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        gap(16.px)
                    }
                }
            ) {
                Button(
                    attrs = {
                        onClick { currentScreen = Screen.MAP }
                        style {
                            padding(8.px, 16.px)
                            backgroundColor(if (currentScreen == Screen.MAP) Color("#1565c0") else Color.Transparent)
                            color(Color.White)
                            border(1.px, LineStyle.Solid, Color.White)
                            borderRadius(4.px)
                            cursor(Cursor.Pointer)
                        }
                    }
                ) {
                    Text("Карта")
                }
                
                Button(
                    attrs = {
                        onClick { currentScreen = Screen.LIST }
                        style {
                            padding(8.px, 16.px)
                            backgroundColor(if (currentScreen == Screen.LIST) Color("#1565c0") else Color.Transparent)
                            color(Color.White)
                            border(1.px, LineStyle.Solid, Color.White)
                            borderRadius(4.px)
                            cursor(Cursor.Pointer)
                        }
                    }
                ) {
                    Text("Список")
                }
                
                Button(
                    attrs = {
                        onClick { viewModel.startAddingLocation() }
                        style {
                            padding(8.px, 16.px)
                            backgroundColor(Color("#4caf50"))
                            color(Color.White)
                            border(1.px, LineStyle.Solid, Color.White)
                            borderRadius(4.px)
                            cursor(Cursor.Pointer)
                        }
                    }
                ) {
                    Text("+ Добавить")
                }
            }
        }
        
        // Main content
        Div(
            attrs = {
                style {
                    flex(1)
                    display(DisplayStyle.Flex)
                    overflow("hidden")
                }
            }
        ) {
            when {
                isAddingLocation -> {
                    AddLocationFormWeb(
                        onSave = { location ->
                            viewModel.saveLocation(location) {
                                viewModel.cancelAddingLocation()
                            }
                        },
                        onCancel = { viewModel.cancelAddingLocation() }
                    )
                }
                selectedLocation != null -> {
                    LocationDetailWeb(
                        location = selectedLocation!!,
                        onBack = { viewModel.selectLocation(null) },
                        onUpdate = { viewModel.updateLocation(it) },
                        onDelete = { viewModel.deleteLocation(it.id) }
                    )
                }
                else -> {
                    when (currentScreen) {
                        Screen.MAP -> {
                            MapViewWeb(
                                locations = locations,
                                onLocationClick = { viewModel.selectLocation(it) }
                            )
                        }
                        Screen.LIST -> {
                            LocationListWeb(
                                locations = locations,
                                filter = currentFilter,
                                onFilterChange = { viewModel.updateFilter(it) },
                                onLocationClick = { viewModel.selectLocation(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Screen {
    MAP, LIST
}
