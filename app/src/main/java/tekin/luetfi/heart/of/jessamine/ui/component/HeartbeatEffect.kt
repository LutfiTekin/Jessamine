package tekin.luetfi.heart.of.jessamine.ui.component
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import tekin.luetfi.heart.of.jessamine.util.isCloseTo

@Composable
fun HeartbeatEffect(
    isBeating: Boolean,
    content: @Composable (Modifier) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val scale = if (isBeating) {
        val infiniteTransition = rememberInfiniteTransition()
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1f, // loop back to base
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 800 // full heartbeat cycle
                    1.0f at 0 // base
                    1.3f at 100 // first thump
                    1.0f at 200 // relax
                    1.2f at 300 // second thump
                    1.0f at 800 // full relax
                },
                repeatMode = RepeatMode.Restart
            )
        ).value
    } else {
        1f
    }

    // Trigger haptic feedback on each beat
    LaunchedEffect(scale) {
        if (isBeating && (scale.isCloseTo(1.3f) || scale.isCloseTo(1.2f))) {
            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
        }
    }

    val beatModifier = Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

    content(beatModifier)
}

