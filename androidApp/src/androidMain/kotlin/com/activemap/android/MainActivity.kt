package com.activemap.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.android.ui.ActiveMapApp
import com.activemap.shared.di.appModule
import com.activemap.android.di.androidModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    private val viewModel: LocationViewModel by inject()
    
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                viewModel.centerOnMe()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                viewModel.centerOnMe()
            }
            else -> {
                // Permission denied
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(appModule, androidModule)
        }
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ActiveMapApp(
                        viewModel = viewModel,
                        onRequestLocationPermission = { requestLocationPermission() }
                    )
                }
            }
        }
    }
    
    private fun requestLocationPermission() {
        when {
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.centerOnMe()
            }
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.centerOnMe()
            }
            else -> {
                locationPermissionRequest.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
        }
    }
}