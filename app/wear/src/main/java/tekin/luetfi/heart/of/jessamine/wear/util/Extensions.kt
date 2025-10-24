package tekin.luetfi.heart.of.jessamine.wear.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun isRoundDevice(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.isScreenRound
}