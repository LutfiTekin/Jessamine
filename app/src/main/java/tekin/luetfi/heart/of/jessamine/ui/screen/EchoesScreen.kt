package tekin.luetfi.heart.of.jessamine.ui.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import tekin.luetfi.heart.of.jessamine.common.ui.LocalConnectivityMonitor
import tekin.luetfi.heart.of.jessamine.common.ui.component.initialScreenText
import tekin.luetfi.heart.of.jessamine.common.ui.viewmodel.EchoesViewModel
import tekin.luetfi.heart.of.jessamine.common.ui.viewmodel.PlaybackViewModel
import tekin.luetfi.heart.of.jessamine.ui.component.AnimatedConfirmation
import tekin.luetfi.heart.of.jessamine.ui.component.GestureSeekOverlay
import tekin.luetfi.heart.of.jessamine.ui.component.HeartbeatEffect
import tekin.luetfi.heart.of.jessamine.common.ui.component.PlayToggleOverlay
import tekin.luetfi.heart.of.jessamine.common.ui.component.locationWarningText
import tekin.luetfi.heart.of.jessamine.ui.component.SpeechHighlighter
import tekin.luetfi.heart.of.jessamine.common.util.beatDurationMillis
import kotlin.text.uppercase
import kotlin.time.Duration.Companion.seconds

@Composable
fun EchoesScreen(modifier: Modifier) {

    //region ViewModels and States
    val echoesViewModel: EchoesViewModel = hiltViewModel()
    val playbackViewModel: PlaybackViewModel = hiltViewModel()
    val isPlaying by playbackViewModel.isPlaying.collectAsStateWithLifecycle()
    val isMediaSectionActive by playbackViewModel.isMediaSessionActive.collectAsStateWithLifecycle()
    val currentCoordinates by echoesViewModel.currentCoordinates.collectAsStateWithLifecycle()
    val audioData by echoesViewModel.audioData.collectAsStateWithLifecycle()
    val speechMarks by echoesViewModel.speechMarks.collectAsStateWithLifecycle()
    val currentPlace by echoesViewModel.currentPlace.collectAsStateWithLifecycle(null)
    val bytes by echoesViewModel.bytesAccumulated.collectAsState()
    val activity = LocalActivity.current
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var isPlaceNameSettled by rememberSaveable { mutableStateOf(false) }
    val connectivityMonitor = LocalConnectivityMonitor.current
    val isConnected by connectivityMonitor.isConnected.collectAsStateWithLifecycle()

    val initialState by remember(isMediaSectionActive, currentPlace) {
        derivedStateOf {
            isMediaSectionActive.not()
                    && playbackViewModel.exoPlayer.mediaItemCount == 0
                    && currentPlace == null
        }
    }

    //endregion

    //region Side Effects
    fun resetUI() {
        playbackViewModel.exoPlayer.clearMediaItems()
        isPlaceNameSettled = false
        echoesViewModel.reset()
    }

    LaunchedEffect(isMediaSectionActive) {
        if (isMediaSectionActive || initialState)
            return@LaunchedEffect
        delay(2.seconds)
        resetUI()
    }


    LaunchedEffect(audioData, isPlaceNameSettled) {
        if (isPlaying)
            return@LaunchedEffect
        //Wait until place name is shown on screen
        if (!isPlaceNameSettled)
            return@LaunchedEffect
        audioData?.let {
            playbackViewModel.playAudio(it)
        }
    }

    //endregion

    //region Back Handler
    BackHandler {
        resetUI()
        if (initialState) {
            playbackViewModel.clearPlayer()
            activity?.finishAffinity()
        }
    }
    //endregion

    //region UI
    Box(modifier = modifier.fillMaxSize()) {
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
                    player = playbackViewModel.exoPlayer,
                    speechMarks = speechMarks
                )
            }

            currentPlace?.let { place ->
                if (isPlaceNameSettled) {
                    HeartbeatEffect(
                        isBeating = isPlaying.not(),
                        beatDurationMillis = bytes.beatDurationMillis
                    ) { modifier ->
                        Text(
                            modifier = modifier,
                            text = place.name.uppercase(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    AnimatedConfirmation(
                        modifier = Modifier,
                        confirmation = place.confirmation,
                        onAnimationEnd = {
                            isPlaceNameSettled = true
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
                text = if (isConnected) initialScreenText() else locationWarningText(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = if (isConnected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isConnected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
        PlayToggleOverlay(
            initialState = initialState,
            onStart = {
                echoesViewModel.getLocationLore(currentCoordinates)
            },
            onReset = {
                resetUI()
            },
            onFinish = {
                backDispatcher?.onBackPressed()
            })
        if (isMediaSectionActive) {
            GestureSeekOverlay(
                modifier = Modifier.fillMaxSize(),
                player = playbackViewModel.exoPlayer
            )
        }
    }
    //endregion

}




