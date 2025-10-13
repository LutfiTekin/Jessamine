package tekin.luetfi.heart.of.jessamine.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tekin.luetfi.heart.of.jessamine.data.local.LocationLore
import tekin.luetfi.heart.of.jessamine.domain.model.SpeechResponse
import tekin.luetfi.simple.map.data.model.Coordinates

interface LocationInfoRepository {


    val speechData : Flow<SpeechResponse?>

    val currentPlace : Flow<String?>

    suspend fun getLocationLore(coordinates: Coordinates): LocationLore
}