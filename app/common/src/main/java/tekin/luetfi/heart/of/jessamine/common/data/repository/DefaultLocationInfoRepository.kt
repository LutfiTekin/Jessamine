package tekin.luetfi.heart.of.jessamine.common.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechResponse
import tekin.luetfi.heart.of.jessamine.common.domain.repository.LocationInfoRepository


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