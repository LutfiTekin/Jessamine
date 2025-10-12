package tekin.luetfi.simple.map.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tekin.luetfi.simple.map.currentLocation
import tekin.luetfi.simple.map.data.model.Coordinates



@Composable
fun DynamicBackground(
    modifier: Modifier = Modifier,
    hasLocationPermission: Boolean
) {

    val context = LocalContext.current
    val currentLocation by context.currentLocation().collectAsStateWithLifecycle(Coordinates.majorCities.random())

    var list by remember(hasLocationPermission, currentLocation) {
        if (hasLocationPermission) {
            mutableStateOf(listOf(currentLocation))
        }else {
            mutableStateOf(Coordinates.majorCities)
        }

    }



    LaunchedEffect(hasLocationPermission) {

    }

    AnimatingMap(modifier, list)


}






