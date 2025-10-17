package tekin.luetfi.heart.of.jessamine.domain.model

import com.squareup.moshi.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tekin.luetfi.heart.of.jessamine.util.WHISPER_SYSTEM_PROMPT

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.2,
    @field:Json("response_format") val responseFormat: ResponseFormat = ResponseFormat()
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
){
    companion object{
        val systemPrompt = ChatMessage(role = "system", content = WHISPER_SYSTEM_PROMPT.trimIndent())
        fun userPrompt(prompt: String) = ChatMessage(role = "user", content = prompt.trimIndent())
    }
}

@Serializable
data class ResponseFormat(
    val type: String = "json_object"
)