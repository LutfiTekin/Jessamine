package tekin.luetfi.heart.of.jessamine.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color


private val ColorScheme = darkColorScheme(
    primary = Color(0xFF808D83),      // muted blue-gray
    secondary = Color(0xFF605542),    // aged brown/bronze
    tertiary = Color(0xFFE2C16E),     // antique gold
    background = Color(0xFF22292F),   // dark steel/graphite
    surface = Color(0xFF2A2D32),      // off-black for overlays
    onPrimary = Color(0xFFB1BFC8),    // misty highlight
    onSecondary = Color(0xFFb66c54),  // red bronze highlight
    onBackground = Color(0xFFE6E1E5), // pale silver for text
    onSurface = Color(0xFFE6E1E5)     // pale silver for text
)



@Composable
fun JessamineTheme(
    content: @Composable () -> Unit
) {


    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}