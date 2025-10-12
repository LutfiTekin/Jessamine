package tekin.luetfi.heart.of.jessamine.util

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import tekin.luetfi.heart.of.jessamine.domain.model.SpeechResponse


@OptIn(UnstableApi::class)
fun playWithExoPlayer(response: SpeechResponse, context: Context) {
    val audioBytes = Base64.decode(response.audioData, Base64.DEFAULT)

    val player = ExoPlayer.Builder(context).build()

    // Create ByteArrayDataSource with the decoded audio
    val byteArrayDataSource = ByteArrayDataSource(audioBytes)

    // Create DataSource.Factory that returns the ByteArrayDataSource
    val dataSourceFactory = DataSource.Factory { byteArrayDataSource }

    // Create ProgressiveMediaSource using the factory
    val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
        .createMediaSource(MediaItem.fromUri(Uri.EMPTY))

    player.setMediaSource(mediaSource)
    player.prepare()
    player.play()
}