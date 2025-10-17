package tekin.luetfi.heart.of.jessamine.domain.repository

import kotlinx.coroutines.flow.Flow
import tekin.luetfi.heart.of.jessamine.data.model.Place
import tekin.luetfi.heart.of.jessamine.domain.model.SpeechResponse


interface LocationInfoRepository {

    val speechData : Flow<SpeechResponse?>
    val currentPlace : Flow<Place?>

    suspend fun updatePlace(place: Place)
    suspend fun updateSpeech(speech: SpeechResponse)
    fun reset()
}