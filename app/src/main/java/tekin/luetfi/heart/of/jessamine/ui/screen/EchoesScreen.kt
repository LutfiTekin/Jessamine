package tekin.luetfi.heart.of.jessamine.ui.screen

import android.media.session.PlaybackState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import tekin.luetfi.heart.of.jessamine.ui.component.SpeechHighlighter
import tekin.luetfi.heart.of.jessamine.util.playWithExoPlayer

@Composable
fun EchoesScreen(modifier: Modifier){

    val viewModel: EchoesViewModel = hiltViewModel()

    val currentCoordinates by viewModel.currentCoordinates.collectAsStateWithLifecycle()

    val locationLore by viewModel.locationLore.collectAsStateWithLifecycle()

    val speech by viewModel.speech.collectAsStateWithLifecycle(null)

    val context = LocalContext.current
    var playerRef by remember { mutableStateOf<ExoPlayer?>(null) }
    val speechMarks = speech?.speechMarks?.chunks?.mapNotNull { chunk ->
        val startTime = (chunk["start_time"] as? Double)?.toLong() ?: return@mapNotNull null
        val endTime = (chunk["end_time"] as? Double)?.toLong() ?: return@mapNotNull null
        val value = chunk["value"] as? String ?: return@mapNotNull null
        Triple(startTime, endTime, value.uppercase())
    } ?: emptyList()

    LaunchedEffect(speech) {
        speech?.let {
            playerRef = playWithExoPlayer(it, context)
            playerRef?.addListener(
                object : Player.Listener{
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED){
                            playerRef = null
                        }
                        super.onPlaybackStateChanged(playbackState)
                    }
                }
            )
        }
    }


    Box(modifier = modifier.fillMaxSize()){
        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            playerRef?.let { player ->
                SpeechHighlighter(
                    modifier = Modifier.padding(24.dp),
                    player = player,
                    speechMarks = speechMarks)
            }

            if (!locationLore.place.isNullOrBlank()){
                Text(text = locationLore.place.orEmpty(), textAlign = TextAlign.Center)
            }
            if (playerRef == null){
                Button(onClick = { viewModel.getLocationLore(currentCoordinates) }) {
                    Text(text = "Listen To The Echoes")
                }
            }

        }
    }


}