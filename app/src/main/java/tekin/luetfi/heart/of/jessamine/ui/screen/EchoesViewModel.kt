package tekin.luetfi.heart.of.jessamine.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tekin.luetfi.heart.of.jessamine.data.local.LocationLore
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

    val speech = locationInfoRepository.speechData

    private val _locationLore = MutableStateFlow(LocationLore())
    val locationLore: StateFlow<LocationLore> = _locationLore



    fun getLocationLore(coordinates: Coordinates){
        viewModelScope.launch {
            locationInfoRepository.getLocationLore(coordinates).runCatching {
                _locationLore.emit(this)
            }

        }
    }

}