package tekin.luetfi.heart.of.jessamine.util

import android.content.Context
import android.media.audiofx.EnvironmentalReverb
import android.net.Uri
import android.util.Base64
import androidx.annotation.OptIn
import androidx.media3.common.AuxEffectInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import tekin.luetfi.heart.of.jessamine.domain.model.SpeechResponse


@OptIn(UnstableApi::class)
fun playWithExoPlayer(response: SpeechResponse, context: Context): ExoPlayer {
    val audioBytes = Base64.decode(response.audioData, Base64.DEFAULT)

    val player = ExoPlayer.Builder(context).build()

    // Create ByteArrayDataSource with the decoded audio
    val byteArrayDataSource = ByteArrayDataSource(audioBytes)

    // Create DataSource.Factory that returns the ByteArrayDataSource
    val dataSourceFactory = DataSource.Factory { byteArrayDataSource }

    // Create ProgressiveMediaSource using the factory
    val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
        .createMediaSource(MediaItem.fromUri(Uri.EMPTY))

    player.apply {
        setMediaSource(mediaSource)
        prepare()
        playWhenReady = true
        setAuxEffectInfo(AuxEffectInfo(environmentalReverb().id, 1.0f))
    }
    return player
}

@OptIn(UnstableApi::class)
fun ExoPlayer.environmentalReverb(): EnvironmentalReverb {
    val environmentalReverb = EnvironmentalReverb(1, audioSessionId)

    environmentalReverb.apply {
        // Make the reverb feel immediately connected to the source sound.
        // A long delay creates a noticeable gap, which we don't want here.
        reverbDelay = 15 // milliseconds [0, 100]

        // Keep the reverb level at maximum for a very prominent effect.
        reverbLevel = 2000 // millibels [-9000, 2000]

        // Significantly lower the direct 'dry' sound level. This makes the
        // effect feel more immersive and less like it's just 'added on'.
        roomLevel = -4000 // millibels [-9000, 0]

        // The heart's sound lingers for a very long time. We need a much
        // longer decay to create that sustained, mystical wash.
        decayTime = 4500 // milliseconds [100, 20000]

        // A short delay for the first reflections is fine.
        reflectionsDelay = 10 // milliseconds [0, 300]

        // We don't need strong early reflections. The smooth, diffuse tail is
        // the most important part of this sound.
        reflectionsLevel = -2000 // millibels [-9000, 1000]


        // Maxing out Diffusion is key. It controls how much the echoes blur
        // together. High diffusion creates a smooth, dense reverb tail instead
        // of distinct echoes.
        diffusion = 1000.toShort() // [0, 1000]


        // Maxing out Density also contributes to the smoothness and complexity
        // of the reverb tail, preventing a "grainy" sound.
        density = 1000.toShort() // [0, 1000]

        enabled = true
    }
    return environmentalReverb
}


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