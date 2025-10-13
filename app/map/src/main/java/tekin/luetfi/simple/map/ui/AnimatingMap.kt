package tekin.luetfi.simple.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import tekin.luetfi.simple.map.data.model.Coordinates

private const val ANIMATION_DELAY = 30000L
const val MAP_ANIMATION_DURATION: Int = 2000

@Composable
fun AnimatingMap(
    modifier: Modifier = Modifier,
    cities: List<Coordinates> = Coordinates.majorCities,
    showPlaceName: Boolean = false,
    currentCoordinates: (Coordinates) -> Unit = {},
    focused: Boolean = false,
    overlayEnabled: Boolean = false
) {

    var selectedCoordinates by remember(cities) { mutableStateOf(cities.first()) }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }


    LaunchedEffect(key1 = currentIndex, key2 = cities) {
        if (cities.size > 1) { // Only run animation if there's more than one city
            delay(ANIMATION_DELAY) // Wait for the specified delay
            currentIndex = (currentIndex + 1) % cities.size // Move to the next city, looping back
        }
        if (currentIndex > cities.lastIndex)
            currentIndex = 0
        selectedCoordinates = cities[currentIndex]
    }

    LaunchedEffect(selectedCoordinates) {
        currentCoordinates(selectedCoordinates)
    }

    // Wrap SimpleCityMap and Text in a Box to allow overlaying Text
    Box(modifier = modifier.fillMaxSize()) { // This Box will contain both map and text
        SimpleCityMap(
            modifier = Modifier.matchParentSize(), // Map fills the Box
            coordinates = selectedCoordinates,
            focused = focused
        )
        if (overlayEnabled) {
            ForegroundOverlay(
                modifier = Modifier.fillMaxSize(),
                isDark = false
            )
        }

        // Text positioned at the bottom right
        if (showPlaceName) {
            Text(
                text = selectedCoordinates.toString(), // Assuming zoneCode() returns a String
                color = Color.Black, // Choose a color that's visible on your map
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Align to bottom right of the Box
                    .padding(16.dp) // Add some padding from the edges
                    .background(
                        color = Color.LightGray.copy(alpha = 0.7f), // Background color
                        shape = RoundedCornerShape(8.dp) // Apply rounded corners
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp) // Padding inside the text background
            )
        }
    }

}