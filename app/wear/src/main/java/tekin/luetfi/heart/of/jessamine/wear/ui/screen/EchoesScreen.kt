package tekin.luetfi.heart.of.jessamine.wear.ui.screen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kotlinx.coroutines.delay
import tekin.luetfi.heart.of.jessamine.common.ui.component.PlayToggleOverlay
import tekin.luetfi.heart.of.jessamine.common.ui.component.initialScreenText
import tekin.luetfi.heart.of.jessamine.common.ui.viewmodel.EchoesViewModel
import tekin.luetfi.heart.of.jessamine.common.ui.viewmodel.PlaybackViewModel
import tekin.luetfi.heart.of.jessamine.wear.ui.component.CurrentWordDisplay
import tekin.luetfi.heart.of.jessamine.wear.ui.component.CurvedPlaceLabel
import tekin.luetfi.heart.of.jessamine.wear.ui.component.RotarySeekOverlay
import kotlin.time.Duration.Companion.seconds

@Composable
fun EchoesScreen(modifier: Modifier = Modifier){
    val focusRequester = remember { FocusRequester() }
    var bezelRotated by remember { mutableIntStateOf(0) }
    val screenText = initialScreenText(bezelRotated)

    val echoesViewModel: EchoesViewModel = hiltViewModel()
    val playbackViewModel: PlaybackViewModel = hiltViewModel()
    val isPlaying by playbackViewModel.isPlaying.collectAsStateWithLifecycle()
    val isMediaSectionActive by playbackViewModel.isMediaSessionActive.collectAsStateWithLifecycle()
    val currentCoordinates by echoesViewModel.currentCoordinates.collectAsStateWithLifecycle()
    val audioData by echoesViewModel.audioData.collectAsStateWithLifecycle()
    val speechMarks by echoesViewModel.speechMarks.collectAsStateWithLifecycle()
    val currentPlace by echoesViewModel.currentPlace.collectAsStateWithLifecycle(null)
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val initialState by remember(isMediaSectionActive, currentPlace) {
        derivedStateOf {
            isMediaSectionActive.not()
                    && playbackViewModel.exoPlayer.mediaItemCount == 0
                    && currentPlace == null
        }
    }



    LaunchedEffect(isPlaying) {
        if (!isMediaSectionActive)
            focusRequester.requestFocus()
    }

    fun resetUI() {
        playbackViewModel.exoPlayer.clearMediaItems()
        echoesViewModel.reset()
    }

    LaunchedEffect(isMediaSectionActive) {
        if (isMediaSectionActive || initialState)
            return@LaunchedEffect
        delay(2.seconds)
        resetUI()
    }


    LaunchedEffect(audioData) {
        if (isPlaying)
            return@LaunchedEffect
        audioData?.let {
            playbackViewModel.playAudio(it)
        }
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background).onRotaryScrollEvent{ _ ->
                bezelRotated++
                true
            }
            .focusRequester(focusRequester)
            .focusable(),
        contentAlignment = Alignment.Center
    ) {
        if (isPlaying){
            CurrentWordDisplay(
                modifier = Modifier,
                player = playbackViewModel.exoPlayer,
                speechMarks = speechMarks)
            CurvedPlaceLabel(currentPlace)
            RotarySeekOverlay(
                player = playbackViewModel.exoPlayer,
                focusRequester = focusRequester)
        }else {
            currentPlace?.let { place ->
                Text(
                    modifier = modifier,
                    text = place.name.uppercase(),
                    style = MaterialTheme.typography.caption1,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }



        if (initialState){
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.Center),
                text = screenText,
                style = MaterialTheme.typography.caption1,
                fontWeight = FontWeight.SemiBold,
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

    }



}