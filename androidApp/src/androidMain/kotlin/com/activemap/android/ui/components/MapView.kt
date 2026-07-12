package com.activemap.android.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.activemap.shared.model.ActivityType
import com.activemap.shared.model.Location
import com.activemap.shared.model.Route
import com.activemap.shared.resources.Strings
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapView(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit,
    onLongPress: (Double, Double) -> Unit = { _, _ -> },
    isRouteMode: Boolean = false,
    routeWaypoints: List<Pair<Double, Double>> = emptyList(),
    selectedRouteLocations: List<Pair<Double, Double>> = emptyList(),
    pickedPoint: Pair<Double, Double>? = null,
    currentRoute: Route? = null,
    onMapReady: (MapView) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentLocations by remember { mutableStateOf(locations) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(locations) {
        currentLocations = locations
    }

    val selectPointText = Strings.selectPoint()
    val routeStartText = Strings.routeStart()
    val routeEndText = Strings.routeEnd()
    val selectRoutePointsText = Strings.selectRoutePoints()

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    setBuiltInZoomControls(false)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(55.7558, 37.6173))
                    onMapReady(this)
                    mapViewRef = this

                    val mapEventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                            if (p == null) return false

                            val tappedLocation = currentLocations.find { loc ->
                                val dx = p.latitude - loc.latitude
                                val dy = p.longitude - loc.longitude
                                dx * dx + dy * dy < 0.001
                            }

                            if (tappedLocation != null) {
                                onLocationClick(tappedLocation)
                                return true
                            }
                            return false
                        }

                        override fun longPressHelper(p: GeoPoint?): Boolean {
                            p?.let {
                                onLongPress(it.latitude, it.longitude)
                            }
                            return true
                        }
                    }
                    overlays.add(0, MapEventsOverlay(mapEventsReceiver))
                }
            },
            update = { mapView ->
                mapView.overlays.removeAll { it is Marker || it is Polyline }

                if (!isRouteMode) {
                    pickedPoint?.let { point ->
                        val pickMarker = Marker(mapView).apply {
                            position = GeoPoint(point.first, point.second)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = selectPointText
                            snippet = "%.6f, %.6f".format(point.first, point.second)
                            setInfoWindow(null)
                        }
                        mapView.overlays.add(pickMarker)
                    }
                }

                if (isRouteMode) {
                    routeWaypoints.forEachIndexed { index, waypoint ->
                        val label = when (index) {
                            0 -> routeStartText
                            routeWaypoints.lastIndex -> routeEndText
                            else -> "${index + 1}"
                        }
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(waypoint.first, waypoint.second)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = label
                            snippet = "%.6f, %.6f".format(waypoint.first, waypoint.second)
                            setInfoWindow(null)
                            setOnMarkerClickListener(Marker.OnMarkerClickListener { _, _ -> true })
                        }
                        mapView.overlays.add(marker)
                    }
                }

                currentRoute?.let { route ->
                    val polyline = Polyline().apply {
                        outlinePaint.color = Color.BLUE
                        outlinePaint.strokeWidth = 8f
                        setPoints(route.points.map { GeoPoint(it.latitude, it.longitude) })
                    }
                    mapView.overlays.add(polyline)

                    val boundingBox = BoundingBox.fromGeoPoints(
                        route.points.map { GeoPoint(it.latitude, it.longitude) }
                    )
                    mapView.zoomToBoundingBox(boundingBox.increaseByScale(1.2f), true)
                }

                locations.forEach { location ->
                    val isSelected = selectedRouteLocations.any {
                        it.first == location.latitude && it.second == location.longitude
                    }
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(location.latitude, location.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = location.name
                        setInfoWindow(null)

                        setOnMarkerClickListener(Marker.OnMarkerClickListener { clickedMarker, _ ->
                            val loc = currentLocations.find {
                                it.latitude == clickedMarker.position.latitude && it.longitude == clickedMarker.position.longitude
                            }
                            if (loc != null) {
                                onLocationClick(loc)
                            }
                            true
                        })

                        if (isRouteMode && isSelected) {
                            val index = selectedRouteLocations.indexOfFirst {
                                it.first == location.latitude && it.second == location.longitude
                            } + 1
                            snippet = "Маршрут: точка $index"

                            val size = 48
                            val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
                            val canvas = android.graphics.Canvas(bitmap)
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.parseColor("#FF9800")
                                isAntiAlias = true
                            }
                            canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, paint)
                            paint.color = android.graphics.Color.BLACK
                            paint.style = android.graphics.Paint.Style.STROKE
                            paint.strokeWidth = 4f
                            canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, paint)
                            paint.color = android.graphics.Color.BLACK
                            paint.style = android.graphics.Paint.Style.FILL
                            paint.textSize = 24f
                            paint.textAlign = android.graphics.Paint.Align.CENTER
                            canvas.drawText("$index", size / 2f, size / 2f + 8f, paint)
                            setIcon(android.graphics.drawable.BitmapDrawable(context.resources, bitmap))
                        } else {
                            snippet = "${location.activityType.name} - ${location.status.name}"
                        }
                    }
                    mapView.overlays.add(marker)
                }

                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )

        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text(
                text = if (isRouteMode) selectRoutePointsText else selectPointText,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
