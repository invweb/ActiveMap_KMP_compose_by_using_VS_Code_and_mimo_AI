package com.activemap.web.ui

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.shared.model.Location
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.shared.resources.Strings
import com.activemap.shared.resources.LocaleManager
import com.activemap.shared.resources.AppLanguage
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
    val isRouteMode by viewModel.isRouteMode.collectAsState()
    val routeStart by viewModel.routeStart.collectAsState()
    val routeEnd by viewModel.routeEnd.collectAsState()
    val currentRoute by viewModel.currentRoute.collectAsState()
    val isCalculatingRoute by viewModel.isCalculatingRoute.collectAsState()
    val routeError by viewModel.routeError.collectAsState()
    val currentLanguage by LocaleManager.currentLanguage.collectAsState()
    
    var currentScreen by remember { mutableStateOf(Screen.MAP) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    
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
                Text(Strings.appName())
            }
            
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        gap(16.px)
                        alignItems(AlignItems.Center)
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
                    Text(Strings.map())
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
                    Text(Strings.list())
                }
                
                Button(
                    attrs = {
                        onClick { viewModel.toggleRouteMode() }
                        style {
                            padding(8.px, 16.px)
                            backgroundColor(if (isRouteMode) Color("#ff9800") else Color.Transparent)
                            color(Color.White)
                            border(1.px, LineStyle.Solid, Color.White)
                            borderRadius(4.px)
                            cursor(Cursor.Pointer)
                        }
                    }
                ) {
                    Text(Strings.routeMode())
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
                    Text(Strings.add())
                }
                
                Div(
                    attrs = {
                        style {
                            position(Position.Relative)
                        }
                    }
                ) {
                    Button(
                        attrs = {
                            onClick { showLanguageMenu = !showLanguageMenu }
                            style {
                                padding(8.px, 16.px)
                                backgroundColor(Color.Transparent)
                                color(Color.White)
                                border(1.px, LineStyle.Solid, Color.White)
                                borderRadius(4.px)
                                cursor(Cursor.Pointer)
                            }
                        }
                    ) {
                        Text(currentLanguage.displayName)
                    }
                    
                    if (showLanguageMenu) {
                        Div(
                            attrs = {
                                style {
                                    position(Position.Absolute)
                                    top(100.percent)
                                    right(0.px)
                                    marginTop(4.px)
                                    backgroundColor(Color.White)
                                    borderRadius(4.px)
                                    boxShadow(0.px, 2.px, 8.px, Color("#00000033"))
                                    overflow("hidden")
                                    zIndex(100)
                                }
                            }
                        ) {
                            AppLanguage.entries.forEach { language ->
                                Div(
                                    attrs = {
                                        onClick {
                                            LocaleManager.setLanguage(language)
                                            showLanguageMenu = false
                                        }
                                        style {
                                            padding(12.px, 16.px)
                                            cursor(Cursor.Pointer)
                                            backgroundColor(if (language == currentLanguage) Color("#e3f2fd") else Color.Transparent)
                                            color(Color("#333"))
                                            hover {
                                                backgroundColor(Color("#f5f5f5"))
                                            }
                                        }
                                    }
                                ) {
                                    Text(language.displayName)
                                }
                            }
                        }
                    }
                }
            }
        }
        
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
                            Box(
                                attrs = {
                                    style {
                                        flex(1)
                                        position(Position.Relative)
                                    }
                                }
                            ) {
                                MapViewWeb(
                                    locations = locations,
                                    onLocationClick = { viewModel.selectLocation(it) },
                                    onMapClick = { x, y ->
                                        if (isRouteMode) {
                                            viewModel.setRoutePoint(x, y)
                                        }
                                    },
                                    isRouteMode = isRouteMode,
                                    routeStart = routeStart,
                                    routeEnd = routeEnd,
                                    currentRoute = currentRoute
                                )
                                
                                if (isCalculatingRoute) {
                                    Div(
                                        attrs = {
                                            style {
                                                position(Position.Absolute)
                                                top(50.percent)
                                                left(50.percent)
                                                transform("translate(-50%, -50%)")
                                                padding(16.px)
                                                backgroundColor(Color.White)
                                                borderRadius(8.px)
                                                boxShadow(0.px, 2.px, 8.px, Color("#00000033"))
                                            }
                                        }
                                    ) {
                                        Text(Strings.routeCalculating())
                                    }
                                }
                                
                                routeError?.let { error ->
                                    Div(
                                        attrs = {
                                            style {
                                                position(Position.Absolute)
                                                bottom(16.px)
                                                left(50.percent)
                                                transform("translateX(-50%)")
                                                padding(12.px)
                                                backgroundColor(Color("#f44336"))
                                                color(Color.White)
                                                borderRadius(4.px)
                                            }
                                        }
                                    ) {
                                        Text(error)
                                    }
                                }
                                
                                currentRoute?.let { route ->
                                    Div(
                                        attrs = {
                                            style {
                                                position(Position.Absolute)
                                                bottom(16.px)
                                                left(50.percent)
                                                transform("translateX(-50%)")
                                                padding(12.px)
                                                backgroundColor(Color("#4caf50"))
                                                color(Color.White)
                                                borderRadius(4.px)
                                            }
                                        }
                                    ) {
                                        Text(Strings.routeInfo(route.distanceKm, route.durationText))
                                    }
                                }
                            }
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
