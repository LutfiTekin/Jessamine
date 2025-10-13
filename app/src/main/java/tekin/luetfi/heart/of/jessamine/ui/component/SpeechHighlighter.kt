package tekin.luetfi.heart.of.jessamine.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

@Composable
fun SpeechHighlighter(
    modifier: Modifier = Modifier,
    player: ExoPlayer,
    speechMarks: List<Triple<Long, Long, String>>
) {
    var currentWordIndex by remember { mutableIntStateOf(0) }

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(player) {
        while (true) {
            val position = player.currentPosition
            val index = speechMarks.indexOfLast { position in it.first..it.second }
            if (index != -1 && index != currentWordIndex) {
                currentWordIndex = index
            }
            delay(16) // Update animation every frame
        }
    }

    LaunchedEffect(currentWordIndex) {
        if (currentWordIndex < 1)
            return@LaunchedEffect
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    // Render with highlighting
    Text(
        modifier = modifier,
        textAlign = TextAlign.Center,
        text =
        buildAnnotatedString {
            speechMarks.forEachIndexed { idx, (_, _, word) ->
                if (idx == currentWordIndex) {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                        append(word)
                    }
                } else {
                    append(word)
                }
                append(" ")
            }
        }
    )
}