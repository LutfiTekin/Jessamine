package tekin.luetfi.heart.of.jessamine.ui.screen

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
import tekin.luetfi.heart.of.jessamine.data.model.Place
import tekin.luetfi.heart.of.jessamine.di.CurrentLocationFlow
import tekin.luetfi.heart.of.jessamine.domain.repository.LocationInfoRepository
import tekin.luetfi.heart.of.jessamine.domain.usecase.GetLocationLoreUseCase
import tekin.luetfi.heart.of.jessamine.util.ByteAccumulationDispatcher
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
            speechData?.speechMarks?.chunks?.mapNotNull { chunk ->
                val startTime = (chunk["start_time"] as? Double)?.toLong() ?: return@mapNotNull null
                val endTime = (chunk["end_time"] as? Double)?.toLong() ?: return@mapNotNull null
                val value = chunk["value"] as? String ?: return@mapNotNull null
                Triple(startTime, endTime, value.uppercase())
            } ?: emptyList()
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