/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package tekin.luetfi.heart.of.jessamine.wear.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.tooling.preview.devices.WearDevices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import tekin.luetfi.heart.of.jessamine.common.di.LocationPermissionFlow
import tekin.luetfi.heart.of.jessamine.wear.ui.component.LocationWarningCurved
import tekin.luetfi.heart.of.jessamine.wear.ui.screen.EchoesScreen
import tekin.luetfi.heart.of.jessamine.wear.ui.theme.JessamineTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @LocationPermissionFlow
    lateinit var locationPermission: MutableStateFlow<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {
            val hasLocationPermission by locationPermission.collectAsStateWithLifecycle()
            if (hasLocationPermission){
                bodySensorsPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.BODY_SENSORS
                    )
                )
            }
            WearApp(hasLocationPermission)
            if (!hasLocationPermission) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            )
                        }) { }
            }
        }
    }




    private val locationPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle the result here
        val granted = permissions.entries.all { it.value } // Check if ALL permissions are granted

        // Update your shared flow with the result
        locationPermission.value = granted
    }

    private val bodySensorsPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle the result here
        val granted = permissions.entries.all { it.value } // Check if ALL permissions are granted
    }



}

@Composable
fun WearApp(hasLocationPermission: Boolean = true) {


    JessamineTheme {
        EchoesScreen()
        if (hasLocationPermission.not()) {
            LocationWarningCurved()
        }

    }
}


@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}