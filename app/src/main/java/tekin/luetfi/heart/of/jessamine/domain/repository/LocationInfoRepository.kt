package tekin.luetfi.heart.of.jessamine.domain.repository

import kotlinx.coroutines.flow.Flow
import tekin.luetfi.heart.of.jessamine.data.model.Place
import tekin.luetfi.heart.of.jessamine.domain.model.SpeechResponse
import tekin.luetfi.simple.map.data.model.Coordinates

interface LocationInfoRepository {

    val speechData : Flow<SpeechResponse?>

    val currentPlace : Flow<Place?>

    suspend fun getLocationLore(coordinates: Coordinates)

    fun resetPlace()
}