package tekin.luetfi.heart.of.jessamine.common.domain.repository

import kotlinx.coroutines.flow.Flow
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechData


interface LocationInfoRepository {

    val speechData : Flow<SpeechData?>
    val currentPlace : Flow<Place?>

    suspend fun updatePlace(place: Place)
    suspend fun updateSpeech(speech: SpeechData)
    fun reset()
}