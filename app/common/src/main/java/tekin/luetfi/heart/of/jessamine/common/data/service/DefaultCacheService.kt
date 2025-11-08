package tekin.luetfi.heart.of.jessamine.common.data.service

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechData
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechMarks
import tekin.luetfi.heart.of.jessamine.common.domain.service.CacheService
import tekin.luetfi.heart.of.jessamine.common.util.PLACE
import tekin.luetfi.heart.of.jessamine.common.util.SPEECH_MARKS
import java.io.File
import java.io.FileNotFoundException

class DefaultCacheService(
    private val context: Context,
    private val dataStore: DataStore<Preferences>,
    moshi: Moshi
): CacheService {

    private val speechMarksAdapter = moshi.adapter(SpeechMarks::class.java)
    private val placeAdapter = moshi.adapter(Place::class.java)
    private val mapAdapter = moshi.adapter(Map::class.java)
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * The audio is not urgently saved to cache.
     * We will use the audio data from the memory for initial playback
     */
    override fun cacheVisitedPlace(speechData: SpeechData, place: Place) {
        scope.launch {
            try {
                val audioBytes = Base64.decode(speechData.audioData, Base64.NO_WRAP)
                val audioFile = File(context.cacheDir, place.key)
                audioFile.writeBytes(audioBytes)
                val metadata = buildMap {
                    put(PLACE, placeAdapter.toJson(place))
                    speechData.speechMarks?.let {
                        put(SPEECH_MARKS, speechMarksAdapter.toJson(it))
                    }
                }

                val json = mapAdapter.toJson(metadata)
                val key = stringPreferencesKey(place.key)

                dataStore.edit { prefs ->
                    prefs[key] = json
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override suspend fun reconstructFromCache(placeKey: String): Map<Place, SpeechData> {
        return withContext(Dispatchers.IO) {
            val audioFile = File(context.cacheDir, placeKey)
            if (!audioFile.exists()) {
                throw FileNotFoundException("Audio file not found: $placeKey")
            }

            // Read audio bytes and encode to Base64
            val audioBytes = audioFile.readBytes()
            val audioBase64 = Base64.encodeToString(audioBytes, Base64.NO_WRAP)

            // Read metadata from DataStore
            val key = stringPreferencesKey(placeKey)
            val metadataJson = dataStore.data
                .map { prefs -> prefs[key] }
                .firstOrNull() ?: throw IllegalStateException("No metadata found for key: $placeKey")

            val metadataMap = mapAdapter.fromJson(metadataJson) ?: emptyMap<String, String>()

            val placeJson = metadataMap[PLACE] as? String ?: throw IllegalStateException("Missing place JSON")
            val speechMarksJson = metadataMap[SPEECH_MARKS] as? String

            val place = placeAdapter.fromJson(placeJson) ?: throw IllegalStateException("Failed to parse place")
            val speechMarks = speechMarksJson?.let { speechMarksAdapter.fromJson(it) }

            val speechResponse = SpeechData(
                audioData = audioBase64,
                speechMarks = speechMarks
            )

            mapOf(place to speechResponse)
        }
    }



    override suspend fun isCacheHit(placeKey: String): Boolean {
        return withContext(Dispatchers.IO) {
            val audioFile = File(context.cacheDir, placeKey)
            audioFile.exists()
        }
    }

    override suspend fun getCachedPlaces(): List<Place> {
        return withContext(Dispatchers.IO) {
            val prefs = dataStore.data.firstOrNull() ?: return@withContext emptyList()

            prefs.asMap().mapNotNull { (_, value) ->
                try {
                    val json = value as? String ?: return@mapNotNull null
                    val metadataMap = mapAdapter.fromJson(json) ?: return@mapNotNull null
                    val placeJson = metadataMap[PLACE] as? String ?: return@mapNotNull null
                    placeAdapter.fromJson(placeJson)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

}