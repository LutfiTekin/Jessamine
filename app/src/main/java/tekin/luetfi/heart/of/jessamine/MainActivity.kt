package tekin.luetfi.heart.of.jessamine

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import tekin.luetfi.heart.of.jessamine.di.LocationPermissionFlow
import tekin.luetfi.heart.of.jessamine.ui.theme.JessamineTheme
import tekin.luetfi.simple.map.hasLocationPermission
import tekin.luetfi.simple.map.ui.DynamicBackground
import javax.inject.Inject

const val LOCATION_PERMISSION_REQUEST_CODE = 1

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    @LocationPermissionFlow
    lateinit var locationPermission: MutableStateFlow<Boolean>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestLocationPermission()
        setContent {
            JessamineTheme {
                val hasPermission by locationPermission.collectAsStateWithLifecycle()
                DynamicBackground(modifier = Modifier, hasLocationPermission = hasPermission)
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

        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
    }

}

fun Activity.requestLocationPermission(){
    if (hasLocationPermission.not()) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JessamineTheme {

    }
}