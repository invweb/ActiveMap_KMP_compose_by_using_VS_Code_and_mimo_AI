package com.activemap.android.ui.components

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Route
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
    routeStart: Pair<Double, Double>? = null,
    routeEnd: Pair<Double, Double>? = null,
    currentRoute: Route? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var pickedPoint by remember { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(55.7558, 37.6173))

                    val mapEventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false

                        override fun longPressHelper(p: GeoPoint?): Boolean {
                            p?.let {
                                if (isRouteMode) {
                                    onLongPress(it.latitude, it.longitude)
                                } else {
                                    pickedPoint = it
                                    onLongPress(it.latitude, it.longitude)
                                }
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
                        position = point
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = Strings.selectPoint()
                        snippet = "%.6f, %.6f".format(point.latitude, point.longitude)
                    }
                    mapView.overlays.add(pickMarker)
                }
                }

                if (isRouteMode) {
                    routeStart?.let { start ->
                        val startMarker = Marker(mapView).apply {
                            position = GeoPoint(start.first, start.second)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = Strings.routeStart()
                            snippet = "%.6f, %.6f".format(start.first, start.second)
                        }
                        mapView.overlays.add(startMarker)
                    }
                    
                    routeEnd?.let { end ->
                        val endMarker = Marker(mapView).apply {
                            position = GeoPoint(end.first, end.second)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = Strings.routeEnd()
                            snippet = "%.6f, %.6f".format(end.first, end.second)
                        }
                        mapView.overlays.add(endMarker)
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
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(location.latitude, location.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = location.name
                        snippet = "${location.activityType.name} - ${location.status.name}"

                        setOnMarkerClickListener { _, _ ->
                            onLocationClick(location)
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        FloatingActionButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = Strings.centerOnMe())
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = if (isRouteMode) Strings.selectRoutePoints() else Strings.selectPoint(),
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
