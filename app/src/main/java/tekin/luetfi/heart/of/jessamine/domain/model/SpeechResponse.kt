package tekin.luetfi.heart.of.jessamine.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpeechMarks(
    val chunks: List<Map<String, Any?>>? = null,
    val end: Long? = null,
    @field:Json(name = "end_time") val endTime: Double? = null,
    val start: Long? = null,
    @field:Json(name = "start_time") val startTime: Double? = null,
    val type: String? = null,
    val value: String? = null
)

@JsonClass(generateAdapter = true)
data class SpeechResponse(
    @field:Json(name = "audio_data") val audioData: String,
    @field:Json(name = "audio_format") val audioFormat: String,
    @field:Json(name = "billable_characters_count") val billableCharactersCount: Long,
    @field:Json(name = "speech_marks") val speechMarks: SpeechMarks? = null
)