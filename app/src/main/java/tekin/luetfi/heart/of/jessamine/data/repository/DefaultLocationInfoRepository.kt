package tekin.luetfi.heart.of.jessamine.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tekin.luetfi.heart.of.jessamine.data.model.Place
import tekin.luetfi.heart.of.jessamine.domain.model.SpeechResponse
import tekin.luetfi.heart.of.jessamine.domain.repository.LocationInfoRepository

class DefaultLocationInfoRepository() : LocationInfoRepository {

    private val _speechData = MutableStateFlow<SpeechResponse?>(null)
    override val speechData = _speechData.asStateFlow()

    private val _currentPlace = MutableStateFlow<Place?>(null)
    override val currentPlace = _currentPlace.asStateFlow()

    override fun reset() {
        _speechData.value = null
        _currentPlace.value = null
    }


    override suspend fun updatePlace(place: Place) {
        _currentPlace.emit(place)
    }

    override suspend fun updateSpeech(speech: SpeechResponse) {
        _speechData.emit(speech)
    }


}