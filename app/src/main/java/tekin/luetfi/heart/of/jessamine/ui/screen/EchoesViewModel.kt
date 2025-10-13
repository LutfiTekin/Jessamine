package tekin.luetfi.heart.of.jessamine.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tekin.luetfi.heart.of.jessamine.data.local.LocationLore
import tekin.luetfi.heart.of.jessamine.data.local.Place
import tekin.luetfi.heart.of.jessamine.di.CurrentLocationFlow
import tekin.luetfi.heart.of.jessamine.domain.repository.LocationInfoRepository
import tekin.luetfi.simple.map.data.model.Coordinates
import javax.inject.Inject

@HiltViewModel
class EchoesViewModel @Inject constructor(
    private val locationInfoRepository: LocationInfoRepository,
    @param:CurrentLocationFlow private val locationFlow: StateFlow<Coordinates>
) : ViewModel() {

    val currentCoordinates = locationFlow

    val currentPlace: Flow<Place?> = locationInfoRepository.currentPlace

    val audioData: StateFlow<String?> = locationInfoRepository.speechData.map { it?.audioData }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val speechMarks: StateFlow<List<Triple<Long, Long, String>>> = locationInfoRepository.speechData.map { speechData ->
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
            locationInfoRepository.getLocationLore(coordinates)
        }
    }

    fun resetPlace() = locationInfoRepository.resetPlace()

}