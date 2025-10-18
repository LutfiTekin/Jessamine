package tekin.luetfi.heart.of.jessamine.domain.usecase

import kotlinx.coroutines.ensureActive
import tekin.luetfi.heart.of.jessamine.domain.repository.LocationInfoRepository
import tekin.luetfi.heart.of.jessamine.domain.service.LLMService
import tekin.luetfi.heart.of.jessamine.domain.service.PlaceService
import tekin.luetfi.heart.of.jessamine.domain.service.TTSService
import tekin.luetfi.simple.map.data.model.Coordinates
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

class GetLocationLoreUseCase @Inject constructor(
    private val placeService: PlaceService,
    private val llmService: LLMService,
    private val ttsService: TTSService,
    private val locationInfoRepository: LocationInfoRepository
) {

    suspend operator fun invoke(coordinates: Coordinates) {
        try {
            val place = placeService.selectPlace(coordinates)
            locationInfoRepository.updatePlace(place)
            coroutineContext.ensureActive()
            val lore = llmService.getLore(place) ?: run {
                locationInfoRepository.reset()
                return
            }
            coroutineContext.ensureActive()
            val speechResponse = ttsService.synthesize(lore)
            locationInfoRepository.updateSpeech(speechResponse)
        } catch (e: Exception) {
            locationInfoRepository.reset()
            if (e is CancellationException) throw e
            e.printStackTrace()
        }
    }

}