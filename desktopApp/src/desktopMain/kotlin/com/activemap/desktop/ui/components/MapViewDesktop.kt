package com.activemap.desktop.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import com.activemap.shared.model.ActivityType
import com.activemap.shared.model.Location
import com.activemap.shared.model.Route
import com.activemap.shared.resources.Strings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.*

@Composable
fun MapViewDesktop(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit,
    onMapClick: (Double, Double) -> Unit = { _, _ -> },
    isRouteMode: Boolean = false,
    routeWaypoints: List<Pair<Double, Double>> = emptyList(),
    selectedRouteLocations: List<Pair<Double, Double>> = emptyList(),
    pickedPoint: Pair<Double, Double>? = null,
    currentRoute: Route? = null,
    modifier: Modifier = Modifier,
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {}
) {
    val noLocationsText = Strings.noLocations()
    val legendText = Strings.legend()
    val sportText = Strings.activitySport()
    val workText = Strings.activityWork()
    val restText = Strings.activityRest()
    val educationText = Strings.activityEducation()
    val entertainmentText = Strings.activityEntertainment()
    val routeStartText = Strings.routeStartMarker()
    val routeEndText = Strings.routeEndMarker()
    val routeText = Strings.routeLine()

    var zoomLevel by remember { mutableStateOf(6) }
    var centerLat by remember { mutableStateOf(48.5) }
    var centerLon by remember { mutableStateOf(31.2) }
    val tileCache = remember { mutableMapOf<String, ImageBitmap>() }
    val scope = rememberCoroutineScope()
    var tileStatus by remember { mutableStateOf("") }

    val allPoints = remember(locations, routeWaypoints, pickedPoint, currentRoute) {
        val pts = mutableListOf<Pair<Double, Double>>()
        locations.forEach { pts.add(it.latitude to it.longitude) }
        routeWaypoints.forEach { pts.add(it) }
        pickedPoint?.let { pts.add(it) }
        currentRoute?.points?.forEach { pts.add(it.latitude to it.longitude) }
        pts
    }

    LaunchedEffect(allPoints) {
        if (allPoints.size == 1) {
            centerLat = allPoints[0].first
            centerLon = allPoints[0].second
        } else if (allPoints.size > 1) {
            centerLat = allPoints.map { it.first }.average()
            centerLon = allPoints.map { it.second }.average()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFE8E8E8))) {
        Canvas(modifier = Modifier.fillMaxSize().clickable { }) {
            val tileW = 256f
            val x0 = lonToTileX(centerLon, zoomLevel)
            val y0 = latToTileY(centerLat, zoomLevel)
            val offX = (x0 - floor(x0)) * tileW
            val offY = (y0 - floor(y0)) * tileW
            val n = 1 shl zoomLevel

            for (tx in -1..(size.width / tileW + 1).toInt()) {
                for (ty in -1..(size.height / tileW + 1).toInt()) {
                    val tileX = floor(x0).toInt() + tx
                    val tileY = floor(y0).toInt() + ty
                    if (tileX < 0 || tileX >= n || tileY < 0 || tileY >= n) continue
                    val key = "$zoomLevel/$tileX/$tileY"
                    val tile = tileCache[key]
                    val dx = tx * tileW - offX
                    val dy = ty * tileW - offY
                    if (tile != null) {
                        drawImage(tile, dstOffset = androidx.compose.ui.unit.IntOffset(dx.toInt(), dy.toInt()),
                            dstSize = androidx.compose.ui.unit.IntSize(tileW.toInt(), tileW.toInt()))
                    } else {
                        drawRect(color = Color(0xFFD0D0D0), topLeft = Offset(dx.toFloat(), dy.toFloat()),
                            size = androidx.compose.ui.geometry.Size(tileW, tileW))
                        scope.launch {
                            try {
                                val url = URL("https://tile.openstreetmap.org/$zoomLevel/$tileX/$tileY.png")
                                val conn = url.openConnection() as HttpURLConnection
                                conn.setRequestProperty("User-Agent", "ActiveMap/1.0 (https://github.com/invweb/ActiveMap)")
                                conn.connectTimeout = 10000
                                conn.readTimeout = 10000
                                val code = withContext(Dispatchers.IO) { conn.responseCode }
                                if (code == 200) {
                                    val img = withContext(Dispatchers.IO) { ImageIO.read(conn.inputStream) }
                                    conn.disconnect()
                                    if (img != null) tileCache[key] = img.toComposeImageBitmap()
                                } else {
                                    conn.disconnect()
                                }
                            } catch (e: Exception) {
                                println("Tile error: $key - ${e.message}")
                            }
                        }
                    }
                }
            }

            if (allPoints.isNotEmpty()) {
                val minLat = allPoints.minOf { it.first }; val maxLat = allPoints.maxOf { it.first }
                val minLon = allPoints.minOf { it.second }; val maxLon = allPoints.maxOf { it.second }
                val latR = (maxLat - minLat).coerceAtLeast(0.001); val lonR = (maxLon - minLon).coerceAtLeast(0.001)
                val pad = 50f; val mw = (size.width - pad * 2); val mh = (size.height - pad * 2)
                val cx = size.width / 2; val cy = size.height / 2
                fun ts(lat: Double, lon: Double) = Offset(
                    cx + ((lon - (minLon + maxLon) / 2) / lonR * mw).toFloat(),
                    cy - ((lat - (minLat + maxLat) / 2) / latR * mh).toFloat()
                )

                currentRoute?.let { r ->
                    if (r.points.size >= 2) for (i in 0 until r.points.size - 1) {
                        drawLine(Color(0xFF1565C0), ts(r.points[i].latitude, r.points[i].longitude),
                            ts(r.points[i + 1].latitude, r.points[i + 1].longitude), strokeWidth = 6f)
                    }
                }
                routeWaypoints.forEachIndexed { i, wp ->
                    val p = ts(wp.first, wp.second)
                    val c = when (i) { 0 -> Color(0xFF4CAF50); routeWaypoints.lastIndex -> Color(0xFFF44336); else -> Color(0xFFFF9800) }
                    drawCircle(c, 12f, p); drawCircle(Color.Black, 12f, p, style = Stroke(2f))
                }
                pickedPoint?.let { val p = ts(it.first, it.second); drawCircle(Color(0xFF00BCD4), 12f, p); drawCircle(Color.Black, 12f, p, style = Stroke(2f)) }
                locations.forEach { loc ->
                    val p = ts(loc.latitude, loc.longitude)
                    val sel = selectedRouteLocations.any { it.first == loc.latitude && it.second == loc.longitude }
                    val c = when { isRouteMode && sel -> Color(0xFFFF9800); else -> when (loc.activityType) {
                        ActivityType.SPORT -> Color.Red; ActivityType.WORK -> Color.Blue; ActivityType.REST -> Color.Green
                        ActivityType.EDUCATION -> Color.Yellow; ActivityType.ENTERTAINMENT -> Color.Magenta
                    }}
                    val r = if (isRouteMode && sel) 20f else 15f
                    drawCircle(c, r, p, style = Fill); drawCircle(Color.Black, r, p, style = Stroke(2f))
                }
            }
        }

        if (locations.isEmpty()) {
            Text(text = noLocationsText, style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF37474F), modifier = Modifier.align(Alignment.Center))
        }

        Card(modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = legendText, style = MaterialTheme.typography.titleSmall)
                LegendItem(sportText, Color.Red); LegendItem(workText, Color.Blue)
                LegendItem(restText, Color.Green); LegendItem(educationText, Color.Yellow)
                LegendItem(entertainmentText, Color.Magenta)
                if (isRouteMode) { Spacer(modifier = Modifier.height(8.dp)); LegendItem(routeStartText, Color(0xFF4CAF50)); LegendItem(routeEndText, Color(0xFFF44336)); LegendItem(routeText, Color(0xFF1565C0)) }
            }
        }

        Column(modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FloatingActionButton(onClick = { zoomLevel = (zoomLevel + 1).coerceAtMost(18); onZoomIn() }, modifier = Modifier.size(40.dp), containerColor = MaterialTheme.colorScheme.primaryContainer) { Text("+") }
            FloatingActionButton(onClick = { zoomLevel = (zoomLevel - 1).coerceAtLeast(2); onZoomOut() }, modifier = Modifier.size(40.dp), containerColor = MaterialTheme.colorScheme.primaryContainer) { Text("\u2212") }
        }
    }
}

private fun lonToTileX(lon: Double, zoom: Int) = (lon + 180.0) / 360.0 * 2.0.pow(zoom.toDouble())
private fun latToTileY(lat: Double, zoom: Int) = (1.0 - ln(tan(Math.toRadians(lat)) + 1.0 / cos(Math.toRadians(lat))) / PI) / 2.0 * 2.0.pow(zoom.toDouble())

@Composable
fun LegendItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Canvas(modifier = Modifier.size(12.dp)) { drawCircle(color = color) }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}
