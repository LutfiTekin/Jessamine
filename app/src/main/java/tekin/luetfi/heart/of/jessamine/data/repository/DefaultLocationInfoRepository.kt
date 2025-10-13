package tekin.luetfi.heart.of.jessamine.data.repository

import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tekin.luetfi.heart.of.jessamine.data.local.LocationLore
import tekin.luetfi.heart.of.jessamine.data.model.Place
import tekin.luetfi.heart.of.jessamine.data.remote.MediaWikiApi
import tekin.luetfi.heart.of.jessamine.data.remote.OpenRouterAiApi
import tekin.luetfi.heart.of.jessamine.data.remote.SpeechifyApi
import tekin.luetfi.heart.of.jessamine.domain.model.ChatMessage
import tekin.luetfi.heart.of.jessamine.domain.model.ChatRequest
import tekin.luetfi.heart.of.jessamine.domain.model.ResponseFormat
import tekin.luetfi.heart.of.jessamine.domain.model.SpeechRequest
import tekin.luetfi.heart.of.jessamine.domain.model.SpeechResponse
import tekin.luetfi.heart.of.jessamine.domain.repository.LocationInfoRepository
import tekin.luetfi.heart.of.jessamine.util.WHISPER_SYSTEM_PROMPT
import tekin.luetfi.heart.of.jessamine.util.geoSearchString
import tekin.luetfi.heart.of.jessamine.util.ssmlText
import tekin.luetfi.simple.map.data.model.Coordinates

class DefaultLocationInfoRepository(
    private val openRouterApi: OpenRouterAiApi,
    private val speechifyApi: SpeechifyApi,
    private val mediaWikiApi: MediaWikiApi,
    private val moshi: Moshi
) : LocationInfoRepository {

    private val _speechData = MutableStateFlow<SpeechResponse?>(null)
    override val speechData = _speechData.asStateFlow()

    suspend fun synthesizeWhispers(lore: String){
        speechifyApi.synthesize(SpeechRequest(lore.ssmlText)).runCatching {
            _speechData.emit(this)
        }
    }

    private val _currentPlace = MutableStateFlow<Place?>(null)
    override val currentPlace = _currentPlace.asStateFlow()

    override fun resetPlace(){
        _currentPlace.value = null
    }


    override suspend fun getLocationLore(coordinates: Coordinates) {
        val geoSearchString = coordinates.geoSearchString
        //Get geo location info
        val geoQuery = try {
            mediaWikiApi.geoSearch(geoSearchCoordinates = geoSearchString, radius = 1400)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        val firstPlace = try {
            geoQuery.query?.geoSearch?.random()
        } catch (e: Exception) {
            null
        }
        val placeName = firstPlace?.title ?: "Unknown Place"

        val selectedCoordinates: Coordinates? = try {
            firstPlace ?: throw Exception("Unknown Place")
            Coordinates(firstPlace.lat, firstPlace.lon)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
        val place = Place(name = placeName, selectedCoordinates)

        _currentPlace.emit(place)

        val messages = listOf(
            ChatMessage(role = "system", content = WHISPER_SYSTEM_PROMPT.trimIndent()),
            ChatMessage(role = "user", content = placeName)
        )

        val request = ChatRequest(
            messages = messages,
            responseFormat = ResponseFormat("json_object"),
            model = "google/gemini-2.0-flash-001"
            //model = "meta-llama/llama-3.3-8b-instruct:free"
        )

        try {
            openRouterApi.getChatCompletion(request).parseResponseOrNull<String>(moshi)
                ?.also {
                    withContext(Dispatchers.IO) {
                        launch {
                            synthesizeWhispers(it)
                        }
                    }
                }
                ?: throw Exception()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}