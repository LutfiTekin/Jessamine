package tekin.luetfi.heart.of.jessamine.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpeechRequest(
    val input: String,
    @field:Json(name = "voice_id") val voiceId: String
)

