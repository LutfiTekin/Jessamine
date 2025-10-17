package tekin.luetfi.heart.of.jessamine.ui.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import tekin.luetfi.heart.of.jessamine.R
import tekin.luetfi.heart.of.jessamine.ui.component.AnimatedConfirmation
import tekin.luetfi.heart.of.jessamine.ui.component.GestureSeekOverlay
import tekin.luetfi.heart.of.jessamine.ui.component.HeartbeatEffect
import tekin.luetfi.heart.of.jessamine.ui.component.SpeechHighlighter
import tekin.luetfi.heart.of.jessamine.util.beatDurationMillis
import kotlin.text.uppercase

@Composable
fun EchoesScreen(modifier: Modifier){

    //region ViewModels and States
    val viewModel: EchoesViewModel = hiltViewModel()
    val playbackViewModel: PlaybackViewModel = hiltViewModel()
    val isPlaying by playbackViewModel.isPlaying.collectAsStateWithLifecycle()
    val isMediaSectionActive by playbackViewModel.isMediaSessionActive.collectAsStateWithLifecycle()
    val currentCoordinates by viewModel.currentCoordinates.collectAsStateWithLifecycle()
    val audioData by viewModel.audioData.collectAsStateWithLifecycle()
    val speechMarks by viewModel.speechMarks.collectAsStateWithLifecycle()
    val playerRef = playbackViewModel.exoPlayer
    val currentPlace by viewModel.currentPlace.collectAsStateWithLifecycle(null)
    val bytes by viewModel.bytesAccumulated.collectAsState()
    val activity = LocalActivity.current

    val initialState = remember(isMediaSectionActive,currentPlace) {
        isMediaSectionActive.not()
                && playbackViewModel.exoPlayer.mediaItemCount == 0
                && currentPlace == null
    }
    //endregion

    //region Side Effects
    LaunchedEffect(isMediaSectionActive) {
        if (isMediaSectionActive || initialState)
            return@LaunchedEffect
        delay(2000L)
        playbackViewModel.exoPlayer.clearMediaItems()
        viewModel.reset()
    }


    LaunchedEffect(audioData) {
        if (isPlaying)
            return@LaunchedEffect
        audioData?.let {
            playbackViewModel.playAudio(it)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            playbackViewModel.clearPlayer()
            viewModel.reset()
        }
    }
    //endregion

    //region Back Handler
    BackHandler {
        playbackViewModel.clearPlayer()
        viewModel.reset()
        activity?.finishAffinity()
    }
    //endregion

    //region UI
    Box(modifier = modifier.fillMaxSize()){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isMediaSectionActive) {
                SpeechHighlighter(
                    modifier = Modifier.padding(24.dp),
                    player = playerRef,
                    speechMarks = speechMarks)
            }

            currentPlace?.let { place ->
                var placeNameSettled by remember(place) { mutableStateOf(false) }
                if (placeNameSettled) {
                    HeartbeatEffect(
                        isBeating = isPlaying.not(),
                        beatDurationMillis = bytes.beatDurationMillis) { modifier ->
                        Text(
                            modifier = modifier,
                            text = place.name.uppercase(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center)
                    }
                }else {
                    AnimatedConfirmation(
                        modifier = Modifier,
                        confirmation = place.confirmation,
                        onAnimationEnd = {
                            placeNameSettled = true
                        })
                }
            }

        }
        if (initialState) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.Center),
                text = initialScreenText(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable {
                    if (!isPlaying) {
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
    //endregion

}

//region Helper Functions
@Composable
fun initialScreenText(): String{
    return arrayOf(
        stringResource(R.string.unseal_the_truth),
        stringResource(R.string.the_stone_remembers),
        stringResource(R.string.let_the_rot_speak),
        stringResource(R.string.hear_the_echoes),
        stringResource(R.string.open_my_eye),
        stringResource(R.string.the_sin_is_deep),
        stringResource(R.string.the_secrets_await),
        stringResource(R.string.let_the_silence_break),
        stringResource(R.string.the_walls_bleed_memory),
        stringResource(R.string.draw_the_story_out)
    ).random().uppercase()
}
//endregion




