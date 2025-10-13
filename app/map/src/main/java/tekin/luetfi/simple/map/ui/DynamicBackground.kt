package tekin.luetfi.simple.map.ui


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tekin.luetfi.simple.map.currentLocation
import tekin.luetfi.simple.map.data.model.Coordinates



@Composable
fun DynamicBackground(
    modifier: Modifier = Modifier,
    hasLocationPermission: Boolean,
    focusedCoordinates: Coordinates?
) {

    val context = LocalContext.current
    val currentLocation by context.currentLocation().collectAsStateWithLifecycle(Coordinates.majorCities.random())

    var list by remember(hasLocationPermission, currentLocation, focusedCoordinates) {
        if (focusedCoordinates != null){
            mutableStateOf(listOf(focusedCoordinates))
        }else if (hasLocationPermission) {
            mutableStateOf(listOf(currentLocation))
        }else {
            mutableStateOf(Coordinates.majorCities)
        }

    }

    AnimatingMap(modifier, list, focused = focusedCoordinates != null)


}






