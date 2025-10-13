package tekin.luetfi.simple.map.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.camera.CameraUpdateFactory.newLatLngBounds
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import tekin.luetfi.simple.map.BuildConfig
import tekin.luetfi.simple.map.data.model.Coordinates
import kotlin.math.roundToInt

@Composable
fun SimpleCityMap(
    modifier: Modifier,
    coordinates: Coordinates,
    focused: Boolean = false,
    userInteractionEnabled: Boolean = false
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    var mapInstance: MapLibreMap? by remember { mutableStateOf(null) }

    // Remember MapView
    val mapView = remember {
        MapLibre.getInstance(context, "", WellKnownTileServer.MapLibre)
        MapView(context).apply { onCreate(null) }
    }

    var mapSettling by remember { mutableStateOf(false) }


    if(focused){
        val azimuth: Double = rememberAzimuth()
        val azimuthKey = remember(azimuth) { (azimuth * 10).roundToInt() / 10.0 } // Delta of 0.1 degrees
        LaunchedEffect(azimuthKey, mapInstance) {
            val map = mapInstance ?: return@LaunchedEffect
            //New position is selected prevent bearing updates
            if (mapSettling) return@LaunchedEffect
            val currentPosition = map.cameraPosition

            val newCameraPosition = CameraPosition.Builder()
                .target(currentPosition.target) // Keep current target
                .zoom(currentPosition.zoom)     // Keep current zoom
                .bearing(azimuth)               // New bearing
                .tilt(currentPosition.tilt)     // Preserve tilt if used
                .build()

            map.easeCamera(
                CameraUpdateFactory.newCameraPosition(newCameraPosition),
                500 // e.g., 1000ms
            )
        }
    }

    // Lifecycle handling
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            mapView
        }
    ) { mv ->
        mv.getMapAsync { map ->
            mapInstance = map
            map.setStyle(BuildConfig.MAP_STYLE) {

                if (!userInteractionEnabled) {
                    map.uiSettings.setAllGesturesEnabled(false)
                } else {
                    map.uiSettings.setAllGesturesEnabled(true)
                }

                // Prevent map from being clickable, focusable, etc.
                // (These are Android View properties, might not be necessary if gestures are off)
                mv.isClickable = userInteractionEnabled
                mv.isFocusable = userInteractionEnabled
                mv.isFocusableInTouchMode = userInteractionEnabled


                // Calculate bounds based on the given coordinate with 2x area
                val baseDelta = 0.01 // Base delta for the area around the coordinate
                val zoomFactor: Double = if (focused) 0.2 else 1.toDouble()
                val expandedDelta = baseDelta * 1.25 * zoomFactor

                val expandedBounds = LatLngBounds.from(
                    coordinates.lat + expandedDelta, // North
                    coordinates.lon + expandedDelta, // East
                    coordinates.lat - expandedDelta, // South
                    coordinates.lon - expandedDelta  // West
                )

                // Set the camera bounds constraint (optional - allows panning within these bounds)
                map.setLatLngBoundsForCameraTarget(expandedBounds)

                // Ease camera to the given coordinates with the expanded bounds
                map.easeCamera(
                    newLatLngBounds(
                        expandedBounds,
                        100 // padding in pixels
                    ),
                    MAP_ANIMATION_DURATION // animation duration in ms
                )
                mapSettling = true
                scope.launch {
                    delay(MAP_ANIMATION_DURATION + 100L)
                    mapSettling = false
                }
            }
        }
    }
}

@Composable
fun rememberAzimuth(alpha: Float = 0.2f): Double { // Adjustable alpha for smoothing strength
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val rotationSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) }

    val rawAzimuth = remember { mutableDoubleStateOf(0.toDouble()) } // Raw before smoothing
    val azimuth = remember { mutableDoubleStateOf(0.toDouble()) } // Smoothed output

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val angle = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    val rawAngle = ((angle + 360) % 360).toDouble() // Normalize raw

                    // Low-pass filter with wrap-around handling for circular data
                    val oldSmoothed = azimuth.doubleValue
                    var delta = rawAngle - oldSmoothed
                    // Adjust delta for shortest angular path
                    if (delta > 180) delta -= 360 else if (delta < -180) delta += 360
                    val smoothedAngle = (oldSmoothed + delta * (1 - alpha)).toDouble()
                    azimuth.doubleValue = (smoothedAngle + 360) % 360 // Re-normalize

                    rawAzimuth.doubleValue = rawAngle // Optional: log raw for comparison
                    println("Raw azimuth: $rawAngle, Smoothed: ${azimuth.doubleValue}")
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return azimuth.doubleValue
}
