package tekin.luetfi.heart.of.jessamine.wear.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

val JessamineWearColors = Colors(
    primary = Color(0xFF808D83),
    primaryVariant = Color(0xFFB1BFC8),
    secondary = Color(0xFF605542),
    secondaryVariant = Color(0xFFb66c54),
    background = Color(0xFF22292F),
    surface = Color(0xFF2A2D32),
    error = Color(0xFFE2C16E), // repurposed as antique gold
    onPrimary = Color(0xFFB1BFC8),
    onSecondary = Color(0xFFb66c54),
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    onError = Color(0xFF22292F), // dark contrast for antique gold
)


@Composable
fun JessamineTheme(
    content: @Composable () -> Unit
) {
    /**
     * Empty theme to customize for your app.
     * See: https://developer.android.com/jetpack/compose/designsystems/custom
     */
    MaterialTheme(

        content = content
    )
}