package tekin.luetfi.heart.of.jessamine.ui.component

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import tekin.luetfi.heart.of.jessamine.R
import tekin.luetfi.heart.of.jessamine.common.util.DEFAULT_BEAT_DURATION_MILLIS
import tekin.luetfi.heart.of.jessamine.common.util.isCloseTo

@Composable
fun HeartbeatEffect(
    isBeating: Boolean,
    beatDurationMillis: Int = DEFAULT_BEAT_DURATION_MILLIS,
    content: @Composable (Modifier) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val playHeartbeatSound = rememberSoundPoolPlayer()

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
        if (isBeating && scale.isCloseTo(1.3f)) {
            playHeartbeatSound()
        }
    }

    val beatModifier = Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

    content(beatModifier)
}

@Composable
fun rememberSoundPoolPlayer(): () -> Unit {
    val context = LocalContext.current
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }

    val soundId = remember {
        soundPool.load(context, R.raw.heartbeat, 1)
    }

    // Release when no longer needed
    DisposableEffect(Unit) {
        onDispose {
            soundPool.release()
        }
    }

    return remember {
        {
            soundPool.play(
                soundId,
                1f,
                1f,
                0,
                0,
                1f)
        }
    }
}


