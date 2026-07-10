package com.activemap.android.ui.components

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.activemap.shared.model.*
import com.activemap.shared.resources.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationForm(
    onSave: (Location) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    initialLat: Double? = null,
    initialLng: Double? = null
) {
    var name by remember { mutableStateOf("") }
    var activityType by remember { mutableStateOf(ActivityType.SPORT) }
    var latitude by remember { mutableStateOf(initialLat?.let { "%.6f".format(it) } ?: "") }
    var longitude by remember { mutableStateOf(initialLng?.let { "%.6f".format(it) } ?: "") }
    var coverage by remember { mutableStateOf(CoverageLevel.MEDIUM) }
    var lighting by remember { mutableStateOf(LightingLevel.MEDIUM) }
    var inventory by remember { mutableStateOf("") }
    var cleanliness by remember { mutableStateOf(CleanlinessLevel.MEDIUM) }
    var noiseLevel by remember { mutableStateOf(NoiseLevel.MEDIUM) }
    var rating by remember { mutableStateOf(3) }
    var status by remember { mutableStateOf(VisitStatus.WANT_TO_VISIT) }
    var notes by remember { mutableStateOf("") }
    var photos by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }

    val nameFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        nameFocusRequester.requestFocus()
        (context as? Activity)?.window?.decorView?.postDelayed({
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(context.window.decorView, InputMethodManager.SHOW_IMPLICIT)
        }, 300)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TopAppBar(
            title = { Text(Strings.addLocation()) },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = Strings.close())
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nameFocusRequester),
                label = { Text(Strings.nameRequired()) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                isError = showError && name.isBlank(),
                supportingText = if (showError && name.isBlank()) {
                    { Text(Strings.nameIsRequired()) }
                } else null
            )

            Text(Strings.activityType(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActivityType.values().forEach { type ->
                    FilterChip(
                        selected = activityType == type,
                        onClick = { activityType = type },
                        label = { Text(type.name) }
                    )
                }
            }

            Text(Strings.coordinatesRequired(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(Strings.latitude()) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    isError = showError && latitude.isBlank()
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(Strings.longitude()) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    isError = showError && longitude.isBlank()
                )
            }

            Text(Strings.coverage(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CoverageLevel.values().forEach { level ->
                    FilterChip(
                        selected = coverage == level,
                        onClick = { coverage = level },
                        label = { Text(level.name) }
                    )
                }
            }

            Text(Strings.lighting(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LightingLevel.values().forEach { level ->
                    FilterChip(
                        selected = lighting == level,
                        onClick = { lighting = level },
                        label = { Text(level.name) }
                    )
                }
            }

            OutlinedTextField(
                value = inventory,
                onValueChange = { inventory = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(Strings.inventory()) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Text(Strings.cleanliness(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CleanlinessLevel.values().forEach { level ->
                    FilterChip(
                        selected = cleanliness == level,
                        onClick = { cleanliness = level },
                        label = { Text(level.name) }
                    )
                }
            }

            Text(Strings.noiseLevel(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NoiseLevel.values().forEach { level ->
                    FilterChip(
                        selected = noiseLevel == level,
                        onClick = { noiseLevel = level },
                        label = { Text(level.name) }
                    )
                }
            }

            Text("${Strings.rating()}: $rating/5", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = rating.toFloat(),
                onValueChange = { rating = it.toInt() },
                valueRange = 1f..5f,
                steps = 3
            )

            Text(Strings.status(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VisitStatus.values().forEach { s ->
                    FilterChip(
                        selected = status == s,
                        onClick = { status = s },
                        label = { Text(s.name) }
                    )
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(Strings.notes()) },
                minLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = photos,
                onValueChange = { photos = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(Strings.photosCommaSeparated()) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Button(
                onClick = {
                    showError = true
                    if (name.isNotBlank() && latitude.isNotBlank() && longitude.isNotBlank()) {
                        val lat = latitude.toDoubleOrNull()
                        val lon = longitude.toDoubleOrNull()
                        if (lat != null && lon != null) {
                            val location = Location(
                                id = System.currentTimeMillis().toString(),
                                name = name,
                                activityType = activityType,
                                latitude = lat,
                                longitude = lon,
                                coverage = coverage,
                                lighting = lighting,
                                inventory = inventory,
                                cleanliness = cleanliness,
                                noiseLevel = noiseLevel,
                                rating = rating,
                                status = status,
                                notes = notes,
                                photos = photos.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            )
                            onSave(location)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(Strings.save())
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
