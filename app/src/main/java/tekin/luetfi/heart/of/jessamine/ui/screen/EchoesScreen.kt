package tekin.luetfi.heart.of.jessamine.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tekin.luetfi.heart.of.jessamine.util.playWithExoPlayer

@Composable
fun EchoesScreen(modifier: Modifier){

    val viewModel: EchoesViewModel = hiltViewModel()

    val currentCoordinates by viewModel.currentCoordinates.collectAsStateWithLifecycle()

    val locationLore by viewModel.locationLore.collectAsStateWithLifecycle()

    val speech by viewModel.speech.collectAsStateWithLifecycle(null)

    val context = LocalContext.current

    LaunchedEffect(speech) {
        speech?.let {
            playWithExoPlayer(it, context)
        }
    }


    Box(modifier = modifier.fillMaxSize()){
        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = locationLore.whispers.joinToString("\n"), textAlign = TextAlign.Center)
            if (!locationLore.place.isNullOrBlank()){
                Text(text = locationLore.place.orEmpty(), textAlign = TextAlign.Center)
            }
            Button(onClick = { viewModel.getLocationLore(currentCoordinates) }) {
                Text(text = "Listen To Echoes")
            }

        }
    }


}