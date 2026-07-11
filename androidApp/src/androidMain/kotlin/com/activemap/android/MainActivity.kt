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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    private val viewModel: LocationViewModel by inject()
    
    private var pendingImportContinuation: ((String?) -> Unit)? = null
    private var pendingExportJson: String? = null
    
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
            }
        }
    }
    
    private val exportDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            val json = pendingExportJson ?: return@let
            contentResolver.openOutputStream(it)?.use { stream ->
                stream.write(json.toByteArray())
            }
        }
        pendingExportJson = null
    }
    
    private val importDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val json = contentResolver.openInputStream(uri)?.use { it.bufferedReader().readText() }
            pendingImportContinuation?.invoke(json)
        } else {
            pendingImportContinuation?.invoke(null)
        }
        pendingImportContinuation = null
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
                        onRequestLocationPermission = { requestLocationPermission() },
                        onExportData = { json ->
                            pendingExportJson = json
                            exportDocumentLauncher.launch("activemap_export.json")
                        },
                        onImportData = {
                            suspendCoroutine<String?> { continuation ->
                                pendingImportContinuation = { json -> continuation.resume(json) }
                                importDocumentLauncher.launch(arrayOf("application/json", "text/*"))
                            }
                        }
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