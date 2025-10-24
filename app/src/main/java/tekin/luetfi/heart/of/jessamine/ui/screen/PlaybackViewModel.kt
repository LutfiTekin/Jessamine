package tekin.luetfi.heart.of.jessamine.ui.screen

import android.app.Application
import androidx.media3.session.MediaSession
import android.net.Uri
import android.util.Base64
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.AuxEffectInfo
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tekin.luetfi.heart.of.jessamine.common.util.environmentalReverbStereo
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val app: Application
): AndroidViewModel(app) {

    private val _exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(app).build().apply {
            val attrs = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
                .build()
            setAudioAttributes(attrs, true)
            setHandleAudioBecomingNoisy(true)

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    _isPlaying.value = playbackState == Player.STATE_READY && playWhenReady
                    if (playbackState == Player.STATE_READY){
                        _isMediaSessionActive.value = true
                    }else if (playbackState == Player.STATE_ENDED){
                        _isMediaSessionActive.value = false
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }
            })
        }
    }

    val exoPlayer: ExoPlayer
        get() = _exoPlayer

    private val _isMediaSessionActive = MutableStateFlow<Boolean>(false)
    val isMediaSessionActive: StateFlow<Boolean> = _isMediaSessionActive


    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val mediaSession: MediaSession = MediaSession.Builder(app, exoPlayer).build()



    @OptIn(UnstableApi::class)
    fun playAudio(audioData: String) {
        viewModelScope.launch {
            try {
                val audioBytes = withContext(Dispatchers.IO){
                    Base64.decode(audioData, Base64.DEFAULT)
                }

                // Create ByteArrayDataSource with the decoded audio
                val byteArrayDataSource = ByteArrayDataSource(audioBytes)

                // Create DataSource.Factory that returns the ByteArrayDataSource
                val dataSourceFactory = DataSource.Factory { byteArrayDataSource }

                // Create ProgressiveMediaSource using the factory
                val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.EMPTY))

                exoPlayer.apply {
                    setMediaSource(mediaSource)
                    prepare()
                    playWhenReady = true
                    setAuxEffectInfo(AuxEffectInfo(environmentalReverbStereo().id, 1.0f))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun clearPlayer(){
        _exoPlayer.stop()
        _exoPlayer.release()
    }


    override fun onCleared() {
        super.onCleared()
        mediaSession.release()
        _exoPlayer.release()
    }
}
