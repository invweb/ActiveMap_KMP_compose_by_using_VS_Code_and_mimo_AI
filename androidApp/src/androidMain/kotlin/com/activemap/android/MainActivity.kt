package com.activemap.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.activemap.shared.repository.InMemoryLocationRepository
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.android.ui.ActiveMapApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val repository = InMemoryLocationRepository()
        val viewModel = LocationViewModel(repository)
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ActiveMapApp(viewModel = viewModel)
                }
            }
        }
    }
}
