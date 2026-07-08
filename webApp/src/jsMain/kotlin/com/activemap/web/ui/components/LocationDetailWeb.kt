package com.activemap.web.ui.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.shared.model.*

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
                overflow("auto")
            }
        }
    ) {
        // Header
        Div(
            attrs = {
                style {
                    display(DisplayStyle.Flex)
                    justifyContent(JustifyContent.SpaceBetween)
                    alignItems(AlignItems.Center)
                    padding(16.px)
                    backgroundColor(Color("#f5f5f5"))
                    borderBottom(1.px, LineStyle.Solid, Color("#e0e0e0"))
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
                        cursor(Cursor.Pointer)
                    }
                }
            ) {
                Text("← Назад")
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
                            color(Color.White)
                            border(1.px, LineStyle.Solid, Color("#1976d2"))
                            borderRadius(4.px)
                            cursor(Cursor.Pointer)
                        }
                    }
                ) {
                    Text(if (isEditing) "Отмена" else "Редактировать")
                }
                
                Button(
                    attrs = {
                        onClick { onDelete(location) }
                        style {
                            padding(8.px, 16.px)
                            backgroundColor(Color("#f44336"))
                            color(Color.White)
                            border(1.px, LineStyle.Solid, Color("#d32f2f"))
                            borderRadius(4.px)
                            cursor(Cursor.Pointer)
                        }
                    }
                ) {
                    Text("Удалить")
                }
            }
        }
        
        // Content
        Div(
            attrs = {
                style {
                    flex(1)
                    padding(16.px)
                    overflow("auto")
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
    // Basic info
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
        Text("Тип: ${location.activityType.name}")
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
        Text("Статус: ${location.status.name}")
    }
    
    // Coordinates
    H2(
        attrs = {
            style {
                margin(0.px, 0.px, 8.px, 0.px)
                fontSize(20.px)
                color(Color("#333"))
            }
        }
    ) {
        Text("Координаты")
    }
    
    P(
        attrs = {
            style {
                margin(0.px, 0.px, 4.px, 0.px)
                color(Color("#666"))
            }
        }
    ) {
        Text("Широта: ${location.latitude}")
    }
    
    P(
        attrs = {
            style {
                margin(0.px, 0.px, 24.px, 0.px)
                color(Color("#666"))
            }
        }
    ) {
        Text("Долгота: ${location.longitude}")
    }
    
    // Ratings
    H2(
        attrs = {
            style {
                margin(0.px, 0.px, 8.px, 0.px)
                fontSize(20.px)
                color(Color("#333"))
            }
        }
    ) {
        Text("Оценки")
    }
    
    RatingRowWeb("Покрытие", location.coverage.name)
    RatingRowWeb("Освещение", location.lighting.name)
    RatingRowWeb("Чистота", location.cleanliness.name)
    RatingRowWeb("Шум", location.noiseLevel.name)
    RatingRowWeb("Рейтинг", "${location.rating}/5")
    
    // Inventory
    H2(
        attrs = {
            style {
                margin(24.px, 0.px, 8.px, 0.px)
                fontSize(20.px)
                color(Color("#333"))
            }
        }
    ) {
        Text("Инвентарь")
    }
    
    P(
        attrs = {
            style {
                margin(0.px, 0.px, 24.px, 0.px)
                color(Color("#666"))
            }
        }
    ) {
        Text(location.inventory.ifEmpty { "Не указан" })
    }
    
    // Notes
    H2(
        attrs = {
            style {
                margin(0.px, 0.px, 8.px, 0.px)
                fontSize(20.px)
                color(Color("#333"))
            }
        }
    ) {
        Text("Заметки")
    }
    
    P(
        attrs = {
            style {
                margin(0.px, 0.px, 24.px, 0.px)
                color(Color("#666"))
            }
        }
    ) {
        Text(location.notes.ifEmpty { "Нет заметок" })
    }
    
    // Photos
    H2(
        attrs = {
            style {
                margin(0.px, 0.px, 8.px, 0.px)
                fontSize(20.px)
                color(Color("#333"))
            }
        }
    ) {
        Text("Фото")
    }
    
    if (location.photos.isEmpty()) {
        P(
            attrs = {
                style {
                    color(Color("#666"))
                }
            }
        ) {
            Text("Нет фото")
        }
    } else {
        location.photos.forEach { photoUrl ->
            P(
                attrs = {
                    style {
                        color(Color("#666"))
                        wordBreak("break-all")
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
                borderBottom(1.px, LineStyle.Solid, Color("#f0f0f0"))
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
                    fontWeight("bold")
                }
            }
        ) {
            Text(value)
        }
    }
}
