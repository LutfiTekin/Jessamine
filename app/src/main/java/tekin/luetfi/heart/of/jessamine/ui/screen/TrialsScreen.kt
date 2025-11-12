package tekin.luetfi.heart.of.jessamine.ui.screen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tekin.luetfi.heart.of.jessamine.common.data.model.Confirmation
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.data.model.Quiz
import tekin.luetfi.heart.of.jessamine.common.ui.LocalConnectivityMonitor
import tekin.luetfi.heart.of.jessamine.common.ui.component.PlayToggleOverlay
import tekin.luetfi.heart.of.jessamine.common.ui.component.locationWarningText
import tekin.luetfi.heart.of.jessamine.common.ui.viewmodel.EchoesViewModel
import tekin.luetfi.heart.of.jessamine.common.ui.viewmodel.PlaybackViewModel
import tekin.luetfi.heart.of.jessamine.ui.component.AnimatedConfirmation
import tekin.luetfi.heart.of.jessamine.ui.component.AnimatedIndeterminateText
import tekin.luetfi.heart.of.jessamine.ui.component.GestureSeekOverlay
import tekin.luetfi.heart.of.jessamine.ui.component.SpeechHighlighter
import tekin.luetfi.heart.of.jessamine.ui.component.failText
import tekin.luetfi.heart.of.jessamine.ui.component.initialQuizScreenText
import tekin.luetfi.heart.of.jessamine.ui.component.loadingTexts
import tekin.luetfi.heart.of.jessamine.ui.component.successText
import tekin.luetfi.heart.of.jessamine.ui.screen.TrialsUiState.Companion.confirmation
import tekin.luetfi.heart.of.jessamine.ui.screen.TrialsUiState.Companion.places
import tekin.luetfi.heart.of.jessamine.ui.viewmodel.TrialsViewModel

@Composable
fun TrialsScreen(modifier: Modifier = Modifier) {

    val echoesViewModel: EchoesViewModel = hiltViewModel()
    val playbackViewModel: PlaybackViewModel = hiltViewModel()
    val trialsViewModel: TrialsViewModel = hiltViewModel()
    val connectivityMonitor = LocalConnectivityMonitor.current
    val isConnected by connectivityMonitor.isConnected.collectAsStateWithLifecycle()
    val audioData by echoesViewModel.audioData.collectAsStateWithLifecycle()
    val isPlaying by playbackViewModel.isPlaying.collectAsStateWithLifecycle()
    val speechMarks by echoesViewModel.speechMarks.collectAsStateWithLifecycle()
    val currentPlace by echoesViewModel.currentPlace.collectAsStateWithLifecycle(null)
    val isMediaSectionActive by playbackViewModel.isMediaSessionActive.collectAsStateWithLifecycle()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val uiState by trialsViewModel.uiState.collectAsStateWithLifecycle()

    val initialState by remember(currentPlace) {
        derivedStateOf {
            currentPlace == null && uiState is TrialsUiState.Initial
        }
    }

    LaunchedEffect(audioData) {
        if (isPlaying)
            return@LaunchedEffect
        audioData?.let {
            playbackViewModel.playAudio(it)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (initialState) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.Center),
                text = if (isConnected) initialQuizScreenText() else locationWarningText(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = if (isConnected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isConnected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        } else {
            when (uiState) {
                is TrialsUiState.Loading -> {
                    AnimatedIndeterminateText(
                        modifier = Modifier
                            .fillMaxWidth(),
                        loadingTexts
                    )
                }

                is TrialsUiState.Correct -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier,
                            text = successText(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        AnimatedConfirmation(
                            modifier = Modifier,
                            confirmation = uiState.confirmation,
                            onAnimationEnd = {

                            })
                    }
                }

                is TrialsUiState.Incorrect -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier,
                            text = failText(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        AnimatedConfirmation(
                            modifier = Modifier,
                            confirmation = uiState.confirmation)
                    }
                }

                is TrialsUiState.Loaded -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .align(Alignment.Center),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (isMediaSectionActive) {
                            SpeechHighlighter(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                player = playbackViewModel.exoPlayer,
                                speechMarks = speechMarks
                            )
                        }else {
                            Text(
                                modifier = Modifier,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                text =
                                    buildString {
                                        speechMarks.forEach {  (_, _, word) ->
                                            append(word)
                                            append(" ")
                                        }
                                    }
                            )
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(uiState.places, key = {
                                it.key
                            }) {

                                AnimatedConfirmation(
                                    modifier = Modifier
                                        .clickable {
                                            trialsViewModel.evaluateAnswer(it)
                                        },
                                    confirmation = it.confirmation,
                                    onAnimationEnd = {

                                    })
                            }
                        }

                    }

                }

                else -> {}
            }
        }
        if (uiState !is TrialsUiState.Loaded) {
            PlayToggleOverlay(
                initialState = initialState,
                onStart = {
                    trialsViewModel.getNewQuiz {
                        echoesViewModel.getLocationLore(it)
                    }
                },
                onReset = {
                    trialsViewModel.reset()
                    echoesViewModel.reset()
                },
                onFinish = {
                    backDispatcher?.onBackPressed()
                })
        }
        if (isMediaSectionActive) {
            GestureSeekOverlay(
                modifier = Modifier.fillMaxSize(),
                player = playbackViewModel.exoPlayer
            )
        }


    }

}


sealed class TrialsUiState(val loadedQuiz: Quiz?) {
    object Initial : TrialsUiState(null)
    object Loading : TrialsUiState(null)
    data class Loaded(val quiz: Quiz) : TrialsUiState(quiz)
    data class Correct(val quiz: Quiz) : TrialsUiState(quiz)
    data class Incorrect(val quiz: Quiz) : TrialsUiState(quiz)

    companion object {
        val TrialsUiState?.confirmation: Confirmation
            get() {
                return this?.loadedQuiz?.correctPlace?.confirmation ?: Confirmation()
            }

        val TrialsUiState?.places: List<Place>
            get() {
                return this?.loadedQuiz?.options ?: emptyList()
            }
    }

}