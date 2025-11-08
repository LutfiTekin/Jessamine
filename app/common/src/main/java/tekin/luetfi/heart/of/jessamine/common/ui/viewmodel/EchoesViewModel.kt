package tekin.luetfi.heart.of.jessamine.common.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.di.CurrentLocationFlow
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechData.Companion.formattedSpeechMarks
import tekin.luetfi.heart.of.jessamine.common.domain.repository.LocationInfoRepository
import tekin.luetfi.heart.of.jessamine.common.domain.usecase.GetLocationLoreUseCase
import tekin.luetfi.heart.of.jessamine.common.util.ByteAccumulationDispatcher
import tekin.luetfi.simple.map.data.model.Coordinates
import javax.inject.Inject

@HiltViewModel
class EchoesViewModel @Inject constructor(
    private val locationInfoRepository: LocationInfoRepository,
    @param:CurrentLocationFlow private val locationFlow: StateFlow<Coordinates>,
    private val byteDispatcher: ByteAccumulationDispatcher,
    private val getLoreUseCase: GetLocationLoreUseCase
) : ViewModel() {

    val currentCoordinates = locationFlow

    val currentPlace: Flow<Place?> = locationInfoRepository.currentPlace

    val bytesAccumulated: StateFlow<Long> = byteDispatcher.bytesAccumulated

    var loreJob: Job? = null

    val audioData: StateFlow<String?> =
        locationInfoRepository.speechData.map { it?.audioData }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val speechMarks: StateFlow<List<Triple<Long, Long, String>>> =
        locationInfoRepository.speechData.map { speechData ->
            speechData.formattedSpeechMarks
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun getLocationLore(coordinates: Coordinates) {
        viewModelScope.launch {
            loreJob?.cancelAndJoin()
            loreJob = launch {
                getLoreUseCase(coordinates)
            }
        }
    }

    fun reset() {
        locationInfoRepository.reset()
        loreJob?.cancel()
    }

    override fun onCleared() {
        reset()
        super.onCleared()
    }

}