package tekin.luetfi.simple.map.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import tekin.luetfi.simple.map.BuildConfig
import tekin.luetfi.simple.map.data.model.Coordinates

@Composable
fun SimpleCityMap(
    modifier: Modifier,
    coordinates: Coordinates,
    userInteractionEnabled: Boolean = false
) {
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    // Remember MapView
    val mapView = remember {
        MapLibre.getInstance(context, "", WellKnownTileServer.MapLibre)
        MapView(context).apply { onCreate(null) }
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
                val expandedDelta = baseDelta * 1.25 // 2x expansion

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
                    org.maplibre.android.camera.CameraUpdateFactory.newLatLngBounds(
                        expandedBounds,
                        100 // padding in pixels
                    ),
                    MAP_ANIMATION_DURATION // animation duration in ms
                )
            }
        }
    }
}