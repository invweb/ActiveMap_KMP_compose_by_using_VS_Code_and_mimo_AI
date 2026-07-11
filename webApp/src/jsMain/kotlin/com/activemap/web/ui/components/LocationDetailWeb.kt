package com.activemap.web.ui.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.web.model.*
import com.activemap.web.model.Strings

@Composable
fun LocationDetailWeb(
    location: Location,
    onBack: () -> Unit,
    onUpdate: (Location) -> Unit,
    onDelete: (Location) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedLocation by remember { mutableStateOf(location) }
    
    Div(
        attrs = {
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                height(100.percent)
                width(100.percent)
                property("overflow", "auto")
            }
        }
    ) {
        Div(
            attrs = {
                style {
                    display(DisplayStyle.Flex)
                    justifyContent(JustifyContent.SpaceBetween)
                    alignItems(AlignItems.Center)
                    padding(16.px)
                    backgroundColor(Color("#f5f5f5"))
                    property("border-bottom", "1px solid #e0e0e0")
                }
            }
        ) {
            Button(
                attrs = {
                    onClick { onBack() }
                    style {
                        padding(8.px, 16.px)
                        backgroundColor(Color("#f5f5f5"))
                        color(Color("#333"))
                        border(1.px, LineStyle.Solid, Color("#ccc"))
                        borderRadius(4.px)
                        property("cursor", "pointer")
                    }
                }
            ) {
                Text(Strings.back())
            }
            
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        gap(8.px)
                    }
                }
            ) {
                Button(
                    attrs = {
                        onClick { isEditing = !isEditing }
                        style {
                            padding(8.px, 16.px)
                            backgroundColor(Color("#2196f3"))
                            color(Color("white"))
                            border(1.px, LineStyle.Solid, Color("#1976d2"))
                            borderRadius(4.px)
                            property("cursor", "pointer")
                        }
                    }
                ) {
                    Text(if (isEditing) Strings.cancel() else Strings.edit())
                }
                
                Button(
                    attrs = {
                        onClick { onDelete(location) }
                        style {
                            padding(8.px, 16.px)
                            backgroundColor(Color("#f44336"))
                            color(Color("white"))
                            border(1.px, LineStyle.Solid, Color("#d32f2f"))
                            borderRadius(4.px)
                            property("cursor", "pointer")
                        }
                    }
                ) {
                    Text(Strings.delete())
                }
            }
        }
        
        Div(
            attrs = {
                style {
                    flex(1)
                    padding(16.px)
                    property("overflow", "auto")
                }
            }
        ) {
            if (isEditing) {
                EditLocationFormWeb(
                    location = editedLocation,
                    onSave = { 
                        onUpdate(it)
                        isEditing = false
                    },
                    onCancel = { isEditing = false }
                )
            } else {
                LocationDetailContentWeb(location)
            }
        }
    }
}

@Composable
fun LocationDetailContentWeb(location: Location) {
    H1(
        attrs = {
            style {
                margin(0.px, 0.px, 16.px, 0.px)
                fontSize(28.px)
                color(Color("#333"))
            }
        }
    ) {
        Text(location.name)
    }
    
    P(
        attrs = {
            style {
                margin(0.px, 0.px, 8.px, 0.px)
                fontSize(18.px)
                color(Color("#666"))
            }
        }
    ) {
        Text("${Strings.type()}: ${location.activityType.name}")
    }
    
    P(
        attrs = {
            style {
                margin(0.px, 0.px, 24.px, 0.px)
                fontSize(18.px)
                color(Color("#666"))
            }
        }
    ) {
        Text("${Strings.status()}: ${location.status.name}")
    }
    
    H2(
        attrs = {
            style {
                margin(0.px, 0.px, 8.px, 0.px)
                fontSize(20.px)
                color(Color("#333"))
            }
        }
    ) {
        Text(Strings.coordinates())
    }
    
    P(
        attrs = {
            style {
                margin(0.px, 0.px, 4.px, 0.px)
                color(Color("#666"))
            }
        }
    ) {
        Text("${Strings.latitude()}: ${location.latitude}")
    }
    
    P(
        attrs = {
            style {
                margin(0.px, 0.px, 24.px, 0.px)
                color(Color("#666"))
            }
        }
    ) {
        Text("${Strings.longitude()}: ${location.longitude}")
    }
    
    H2(
        attrs = {
            style {
                margin(0.px, 0.px, 8.px, 0.px)
                fontSize(20.px)
                color(Color("#333"))
            }
        }
    ) {
        Text(Strings.ratings())
    }
    
    RatingRowWeb(Strings.coverage(), location.coverage.name)
    RatingRowWeb(Strings.lighting(), location.lighting.name)
    RatingRowWeb(Strings.cleanliness(), location.cleanliness.name)
    RatingRowWeb(Strings.noiseLevel(), location.noiseLevel.name)
    RatingRowWeb(Strings.rating(), "${location.rating}/5")
    
    H2(
        attrs = {
            style {
                margin(24.px, 0.px, 8.px, 0.px)
                fontSize(20.px)
                color(Color("#333"))
            }
        }
    ) {
        Text(Strings.inventory())
    }
    
    P(
        attrs = {
            style {
                margin(0.px, 0.px, 24.px, 0.px)
                color(Color("#666"))
            }
        }
    ) {
        Text(location.inventory.ifEmpty { Strings.notSpecified() })
    }
    
    H2(
        attrs = {
            style {
                margin(0.px, 0.px, 8.px, 0.px)
                fontSize(20.px)
                color(Color("#333"))
            }
        }
    ) {
        Text(Strings.notes())
    }
    
    P(
        attrs = {
            style {
                margin(0.px, 0.px, 24.px, 0.px)
                color(Color("#666"))
            }
        }
    ) {
        Text(location.notes.ifEmpty { Strings.noNotes() })
    }
    
    H2(
        attrs = {
            style {
                margin(0.px, 0.px, 8.px, 0.px)
                fontSize(20.px)
                color(Color("#333"))
            }
        }
    ) {
        Text(Strings.photos())
    }
    
    if (location.photos.isEmpty()) {
        P(
            attrs = {
                style {
                    color(Color("#666"))
                }
            }
        ) {
            Text(Strings.noPhotos())
        }
    } else {
        location.photos.forEach { photoUrl ->
            P(
                attrs = {
                    style {
                        color(Color("#666"))
                        property("word-break", "break-all")
                    }
                }
            ) {
                Text(photoUrl)
            }
        }
    }
}

@Composable
fun RatingRowWeb(label: String, value: String) {
    Div(
        attrs = {
            style {
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceBetween)
                padding(8.px, 0.px)
                property("border-bottom", "1px solid #f0f0f0")
            }
        }
    ) {
        Span(
            attrs = {
                style {
                    color(Color("#666"))
                }
            }
        ) {
            Text(label)
        }
        
        Span(
            attrs = {
                style {
                    color(Color("#333"))
                    fontWeight(700)
                }
            }
        ) {
            Text(value)
        }
    }
}
