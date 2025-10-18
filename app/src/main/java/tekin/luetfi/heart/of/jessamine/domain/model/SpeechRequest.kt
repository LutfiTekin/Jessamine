package tekin.luetfi.heart.of.jessamine.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import tekin.luetfi.heart.of.jessamine.BuildConfig

@JsonClass(generateAdapter = true)
data class SpeechRequest(
    val input: String,
    @Json(name = "voice_id") val voiceId: String = BuildConfig.SPEECHIFY_VOICE_ID
)

