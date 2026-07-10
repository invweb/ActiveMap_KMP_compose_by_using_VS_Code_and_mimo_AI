package com.activemap.desktop.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.activemap.shared.model.*
import com.activemap.shared.model.LocationFilter
import com.activemap.shared.resources.Strings

@Composable
fun LocationListDesktop(
    locations: List<Location>,
    filter: LocationFilter,
    onFilterChange: (LocationFilter) -> Unit,
    onLocationClick: (Location) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Search
        OutlinedTextField(
            value = filter.searchQuery,
            onValueChange = { 
                onFilterChange(filter.copy(searchQuery = it))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(Strings.searchPlaceholder()) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = Strings.search()) },
            singleLine = true
        )
        
        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filter.activityType == null,
                onClick = { 
                    onFilterChange(filter.copy(activityType = null))
                },
                label = { Text(Strings.allTypes()) }
            )
            ActivityType.values().forEach { type ->
                FilterChip(
                    selected = filter.activityType == type,
                    onClick = { 
                        onFilterChange(filter.copy(activityType = type))
                    },
                    label = { Text(type.name) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Status filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filter.status == null,
                onClick = { 
                    onFilterChange(filter.copy(status = null))
                },
                label = { Text(Strings.allStatuses()) }
            )
            VisitStatus.values().forEach { status ->
                FilterChip(
                    selected = filter.status == status,
                    onClick = { 
                        onFilterChange(filter.copy(status = status))
                    },
                    label = { Text(status.name) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Location list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(locations) { location ->
                LocationListItemDesktop(
                    location = location,
                    onClick = { onLocationClick(location) }
                )
            }
        }
    }
}

@Composable
fun LocationListItemDesktop(
    location: Location,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${Strings.type()}: ${location.activityType.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${Strings.status()}: ${location.status.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${Strings.rating()}: ${location.rating}/5",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
