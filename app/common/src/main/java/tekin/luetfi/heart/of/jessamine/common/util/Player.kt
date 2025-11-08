package tekin.luetfi.heart.of.jessamine.common.util

import android.media.audiofx.EnvironmentalReverb
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer


@OptIn(UnstableApi::class)
fun ExoPlayer.environmentalReverbStereo(): EnvironmentalReverb { // Renamed for clarity

    // A single EnvironmentalReverb is the correct tool for creating a stereo space.
    // It processes the sound to simulate reflections in a 3D environment,
    // which is inherently a stereo effect.
    val spatialReverb = EnvironmentalReverb(1, audioSessionId).apply {
        // --- Core Settings for a "Void-like" Space ---

        // A long decay time is the most crucial parameter for that Dishonored feel.
        decayTime = 4500 // milliseconds [100, 20000]

        // Keep the reverb very prominent ("wet").
        reverbLevel = 2000 // millibels [-9000, 2000]

        // Make the original ("dry") sound much quieter to feel more distant.
        roomLevel = -4000 // millibels [-9000, 0]


        // --- Fine-Tuning for Smoothness ---

        // Max out diffusion and density. This is critical. It blends the echoes
        // into a smooth, continuous wash of sound, avoiding distinct repeats.
        diffusion = 1000.toShort() // [0, 1000]
        density = 1000.toShort()   // [0, 1000]

        // A very short initial delay makes the reverb feel like part of the original sound.
        reverbDelay = 15 // milliseconds [0, 100]

        // Weaken the early reflections to emphasize the long, smooth reverb tail.
        reflectionsLevel = -2000 // millibels [-9000, 1000]
        reflectionsDelay = 10    // milliseconds [0, 300]

        enabled = true
    }

    return spatialReverb
}