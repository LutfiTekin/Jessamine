package tekin.luetfi.heart.of.jessamine.common.domain.service

import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechData

interface CacheService {

    fun cacheVisitedPlace(speechData: SpeechData, place: Place)

    suspend fun reconstructFromCache(placeKey: String): Map<Place, SpeechData>

    suspend fun isCacheHit(placeKey: String): Boolean

    suspend fun getCachedPlaces(): List<Place>

}