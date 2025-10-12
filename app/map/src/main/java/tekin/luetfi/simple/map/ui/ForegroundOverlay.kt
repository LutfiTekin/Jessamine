package tekin.luetfi.simple.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ForegroundOverlay(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val alpha = 0.8f

    val overlayColor = if (isDark) {
        // dark surface with dynamic alpha
        MaterialTheme.colorScheme.surface.copy(alpha = alpha)
    } else {
        // light variant (white glass look)
        Color.White.copy(alpha = alpha)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(overlayColor)
            .blur(16.dp)
    )
}