package tekin.luetfi.heart.of.jessamine.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun GestureSeekOverlay(
    modifier: Modifier = Modifier,
    player: ExoPlayer,
    seekAmountMs: Long = 300L // Amount to seek per drag tick
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        // 1. Wait for touch down
                        val downEvent = awaitPointerEvent()
                        val downChange = downEvent.changes.firstOrNull { it.pressed }

                        if (downChange != null) {
                            player.playWhenReady = false // Pause on touch down

                            var totalDrag = 0f

                            // 2. Track drag until touch is released
                            var shouldContinue = true
                            while (shouldContinue) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull()

                                // Check if any pointer is still pressed
                                shouldContinue = event.changes.any { it.pressed }

                                // Accumulate horizontal drag if a change is present
                                val dragDelta = change?.positionChange()?.x ?: 0f
                                totalDrag += dragDelta

                                // Trigger seek every 100px dragged
                                val seekSteps = (totalDrag / 100f).toInt()
                                if (seekSteps != 0) {
                                    val seekMs = seekSteps * seekAmountMs
                                    // Use coerceIn for safety, though ExoPlayer handles boundaries
                                    val newPosition = (player.currentPosition + seekMs).coerceIn(0, player.duration)
                                    player.seekTo(newPosition)
                                    totalDrag -= seekSteps * 100f // Reset drag after seek
                                }

                                change?.consume()
                            }

                            player.playWhenReady = true // Resume on release
                        }
                    }
                }
            }
    )
}