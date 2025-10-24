package tekin.luetfi.heart.of.jessamine.wear.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun RotarySeekOverlay(
    modifier: Modifier = Modifier,
    player: ExoPlayer,
    focusRequester: FocusRequester,
    seekAmountMs: Long = 600L // Amount to seek per rotary tick
) {
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .onRotaryScrollEvent { event ->
                val delta = event.verticalScrollPixels
                val seekMs = if (delta > 0) seekAmountMs else -seekAmountMs
                val newPosition = (player.currentPosition + seekMs).coerceIn(0, player.duration)
                player.seekTo(newPosition)
                true // Event consumed
            }
            .focusRequester(focusRequester)
            .focusable(),
        contentAlignment = Alignment.Center
    ) {
        // Optional: Add visual feedback or icons here
    }
}
