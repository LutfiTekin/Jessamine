package tekin.luetfi.heart.of.jessamine.common.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechData
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechData.Companion.toSpeechData
import tekin.luetfi.heart.of.jessamine.common.domain.repository.LocationInfoRepository
import tekin.luetfi.heart.of.jessamine.common.domain.service.LLMService
import tekin.luetfi.heart.of.jessamine.common.domain.service.CacheService
import tekin.luetfi.heart.of.jessamine.common.domain.service.PlaceService
import tekin.luetfi.heart.of.jessamine.common.domain.service.TTSService
import tekin.luetfi.heart.of.jessamine.common.util.placeNameSettleAnimationDuration
import tekin.luetfi.simple.map.data.model.Coordinates
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GetLocationLoreUseCase @Inject constructor(
    private val placeService: PlaceService,
    private val llmService: LLMService,
    private val ttsService: TTSService,
    private val cacheService: CacheService,
    private val locationInfoRepository: LocationInfoRepository
) {

    suspend operator fun invoke(coordinates: Coordinates) {
        processPlaceInformation {
            resolvePlace(coordinates)
        }
    }

    suspend operator fun invoke(place: Place){
        processPlaceInformation {
            place
        }
    }

    private suspend fun resolvePlace(coordinates: Coordinates): Place {
        val place = placeService.selectPlace(coordinates)
        return if (place.name == Place.UNKNOWN) {
            loadRandomCachedPlace(place.coordinates ?: coordinates)
        } else place
    }

    private suspend fun processPlaceInformation(loadedPlace: suspend () -> Place){
        try {
            val place = loadedPlace()
            if (cacheHit(place)) {
                return
            }
            locationInfoRepository.updatePlace(place)
            currentCoroutineContext().ensureActive()
            val lore = llmService.getLore(place) ?: run {
                locationInfoRepository.reset()
                return
            }
            currentCoroutineContext().ensureActive()
            val speechData = ttsService.synthesize(lore).toSpeechData()
            locationInfoRepository.updateSpeech(speechData)
            cacheService.cacheVisitedPlace(speechData, place)
        } catch (e: Exception) {
            locationInfoRepository.reset()
            if (e is CancellationException) throw e
            e.printStackTrace()
        }
    }

    private suspend fun cacheHit(place: Place): Boolean{
        if (cacheService.isCacheHit(place.key).not())
            return false
        try {
            val (place: Place, speechResponse: SpeechData) =
                cacheService.reconstructFromCache(place.key).entries.first()
            locationInfoRepository.updatePlace(place)
            // Wait before loading speech to stay synchronized with the heartbeat effect
            delay(placeNameSettleAnimationDuration)
            locationInfoRepository.updateSpeech(speechResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private suspend fun loadRandomCachedPlace(fallbackCoordinates: Coordinates): Place{
        val cachedPlaces = cacheService.getCachedPlaces()
        if (cachedPlaces.isEmpty()) return Place.unknown(fallbackCoordinates)
        return withContext(Dispatchers.IO){
            val randomPlace = cachedPlaces.random()
            val mergedConfirmation = randomPlace.confirmation.copy(
                items = cachedPlaces.map { it.confirmation.list }.flatten().toSet().toList()
            )
            randomPlace.copy(confirmation = mergedConfirmation)
        }
    }


}