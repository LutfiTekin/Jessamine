package tekin.luetfi.heart.of.jessamine.domain.service

import tekin.luetfi.heart.of.jessamine.data.model.Place
import tekin.luetfi.heart.of.jessamine.domain.model.SpeechResponse

interface CacheService {

    fun cacheVisitedPlace(speechResponse: SpeechResponse, place: Place)

    suspend fun reconstructFromCache(placeKey: String): Map<Place, SpeechResponse>

    suspend fun isCacheHit(placeKey: String): Boolean

}