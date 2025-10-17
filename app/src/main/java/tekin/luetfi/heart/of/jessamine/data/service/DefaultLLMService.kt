package tekin.luetfi.heart.of.jessamine.data.service

import com.squareup.moshi.Moshi
import tekin.luetfi.heart.of.jessamine.BuildConfig
import tekin.luetfi.heart.of.jessamine.data.remote.OpenRouterAiApi
import tekin.luetfi.heart.of.jessamine.domain.model.ChatMessage
import tekin.luetfi.heart.of.jessamine.domain.model.ChatRequest
import tekin.luetfi.heart.of.jessamine.domain.model.ResponseFormat
import tekin.luetfi.heart.of.jessamine.domain.service.LLMService

class DefaultLLMService(
    private val openRouterAiApi: OpenRouterAiApi,
    private val moshi: Moshi
): LLMService {


    override suspend fun getLore(placeName: String): String? {
        val messages = listOf(
            ChatMessage.systemPrompt,
            ChatMessage.userPrompt(placeName)
        )

        val request = ChatRequest(
            messages = messages,
            responseFormat = ResponseFormat(),
            model = BuildConfig.LLM_MODEL
        )

        return try {
            openRouterAiApi
                .getChatCompletion(request)
                .parseResponseOrNull<String>(moshi)
                ?: throw Exception()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}