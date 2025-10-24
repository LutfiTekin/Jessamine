package tekin.luetfi.heart.of.jessamine.common.domain.service

import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechResponse

interface CacheService {

    fun cacheVisitedPlace(speechResponse: SpeechResponse, place: Place)

    suspend fun reconstructFromCache(placeKey: String): Map<Place, SpeechResponse>

    suspend fun isCacheHit(placeKey: String): Boolean

    suspend fun getCachedPlaces(): List<Place>

}