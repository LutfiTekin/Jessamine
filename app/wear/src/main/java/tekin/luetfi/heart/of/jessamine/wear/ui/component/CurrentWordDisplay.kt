package tekin.luetfi.heart.of.jessamine.wear.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

@Composable
fun CurrentWordDisplay(
    modifier: Modifier = Modifier,
    player: ExoPlayer,
    speechMarks: List<Triple<Long, Long, String>>
) {
    var currentWordIndex by remember { mutableIntStateOf(-1) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(player) {
        while (true) {
            val position = player.currentPosition
            val index = speechMarks.indexOfLast { position in it.first..it.second }
            if (index != -1 && index != currentWordIndex) {
                currentWordIndex = index
            }
            delay(32) // Slightly slower for Wear battery optimization
        }
    }

    LaunchedEffect(currentWordIndex) {
        if (currentWordIndex >= 0) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    val currentWord = speechMarks.getOrNull(currentWordIndex)?.third.orEmpty()

    Text(
        modifier = modifier,
        text = currentWord,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    )
}
