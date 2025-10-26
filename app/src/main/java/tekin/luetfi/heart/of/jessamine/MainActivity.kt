package tekin.luetfi.heart.of.jessamine

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.di.LocationPermissionFlow
import tekin.luetfi.heart.of.jessamine.common.ui.LocalConnectivityMonitor
import tekin.luetfi.heart.of.jessamine.common.util.LOCATION_PERMISSION_REQUEST_CODE
import tekin.luetfi.heart.of.jessamine.common.util.requestLocationPermission
import tekin.luetfi.heart.of.jessamine.ui.screen.EchoesScreen
import tekin.luetfi.heart.of.jessamine.common.ui.viewmodel.EchoesViewModel
import tekin.luetfi.heart.of.jessamine.common.util.ConnectivityMonitor
import tekin.luetfi.heart.of.jessamine.common.util.NetworkMonitor
import tekin.luetfi.heart.of.jessamine.ui.theme.JessamineTheme
import tekin.luetfi.simple.map.hasLocationPermission
import tekin.luetfi.simple.map.ui.DynamicBackground
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    @LocationPermissionFlow
    lateinit var locationPermission: MutableStateFlow<Boolean>

    @Inject
    lateinit var connectivityMonitor: ConnectivityMonitor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestLocationPermission()
        if (hasLocationPermission)
            enterFullscreen()
        setContent {
            JessamineTheme {
                val hasPermission by locationPermission.collectAsStateWithLifecycle()
                val echoesViewModel: EchoesViewModel = hiltViewModel()
                val currentPlace: Place? by echoesViewModel.currentPlace.collectAsStateWithLifecycle(
                    Place(""))
                CompositionLocalProvider(LocalConnectivityMonitor provides connectivityMonitor) {
                    DynamicBackground(
                        modifier = Modifier,
                        hasLocationPermission = hasPermission,
                        focusedCoordinates = currentPlace?.coordinates
                    )
                    EchoesScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        val granted = requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED

        locationPermission.value = granted
        if (granted)
            enterFullscreen()

        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
    }

}


fun Activity.enterFullscreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Instead of setDecorFitsSystemWindows, use WindowInsetsController directly
        window.insetsController?.apply {
            hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
    }
}
