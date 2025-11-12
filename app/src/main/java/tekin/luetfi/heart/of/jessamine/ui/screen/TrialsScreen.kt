package tekin.luetfi.heart.of.jessamine.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tekin.luetfi.heart.of.jessamine.common.ui.LocalConnectivityMonitor
import tekin.luetfi.heart.of.jessamine.common.ui.component.PlayToggleOverlay
import tekin.luetfi.heart.of.jessamine.common.ui.component.locationWarningText
import tekin.luetfi.heart.of.jessamine.common.ui.viewmodel.EchoesViewModel
import tekin.luetfi.heart.of.jessamine.ui.component.initialQuizScreenText
import tekin.luetfi.heart.of.jessamine.ui.viewmodel.TrialsViewModel

@Composable
fun TrialsScreen(modifier: Modifier = Modifier){

    val echoesViewModel: EchoesViewModel = hiltViewModel()
    val trialsViewModel: TrialsViewModel = hiltViewModel()
    val connectivityMonitor = LocalConnectivityMonitor.current
    val isConnected by connectivityMonitor.isConnected.collectAsStateWithLifecycle()


    val currentPlace by echoesViewModel.currentPlace.collectAsStateWithLifecycle(null)

    val initialState by remember(currentPlace) {
        derivedStateOf {
            currentPlace == null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
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
        }
        PlayToggleOverlay(
            initialState = initialState,
            onStart = {
                trialsViewModel.getNewQuiz {
                    echoesViewModel.getLocationLore(it)
                }
            },
            onReset = {

            },
            onFinish = {

            })


    }

}