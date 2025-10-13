package tekin.luetfi.heart.of.jessamine.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.exoplayer.ExoPlayer
import tekin.luetfi.heart.of.jessamine.ui.component.GestureSeekOverlay
import tekin.luetfi.heart.of.jessamine.ui.component.SpeechHighlighter

@Composable
fun EchoesScreen(modifier: Modifier){

    val viewModel: EchoesViewModel = hiltViewModel()
    val playbackViewModel: PlaybackViewModel = hiltViewModel()
    val isPlaying by playbackViewModel.isPlaying.collectAsStateWithLifecycle()
    val isMediaSectionActive by playbackViewModel.isMediaSessionActive.collectAsStateWithLifecycle()
    val currentCoordinates by viewModel.currentCoordinates.collectAsStateWithLifecycle()
    val audioData by viewModel.audioData.collectAsStateWithLifecycle()
    val speechMarks by viewModel.speechMarks.collectAsStateWithLifecycle()
    val playerRef = playbackViewModel.exoPlayer
    val currentPlace by viewModel.currentPlace.collectAsStateWithLifecycle(null)



    LaunchedEffect(audioData) {
        if (isPlaying)
            return@LaunchedEffect
        audioData?.let {
            playbackViewModel.playAudio(it)
        }
    }


    Box(modifier = modifier.fillMaxSize()){
        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isMediaSectionActive) {
                SpeechHighlighter(
                    modifier = Modifier.padding(24.dp),
                    player = playerRef,
                    speechMarks = speechMarks)
            }

            currentPlace?.name?.let {
                Text(
                    text = it.uppercase(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold)
            }


        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable{
                    if (!isPlaying){
                        viewModel.getLocationLore(currentCoordinates)
                    }
                }) {
        }
        if (isMediaSectionActive) {
            GestureSeekOverlay(
                modifier = Modifier.fillMaxSize(),
                player = playerRef
            )
        }
    }



}




