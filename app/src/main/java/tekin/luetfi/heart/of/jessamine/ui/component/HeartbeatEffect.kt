package tekin.luetfi.heart.of.jessamine.ui.component
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import tekin.luetfi.heart.of.jessamine.util.isCloseTo

@Composable
fun HeartbeatEffect(
    isBeating: Boolean,
    beatDurationMillis: Int = 800,
    content: @Composable (Modifier) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val scale = if (isBeating) {
        // Key: use a unique key to restart animation when beatDurationMillis changes
        val transition = remember(beatDurationMillis) { Animatable(1f) }

        LaunchedEffect(beatDurationMillis) {
            while (true) {
                transition.animateTo(
                    targetValue = 1.3f,
                    animationSpec = tween(beatDurationMillis / 8)
                )
                transition.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(beatDurationMillis / 8)
                )
                transition.animateTo(
                    targetValue = 1.2f,
                    animationSpec = tween(beatDurationMillis / 4)
                )
                transition.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(beatDurationMillis / 2)
                )
            }
        }

        transition.value
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

