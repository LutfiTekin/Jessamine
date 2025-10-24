package tekin.luetfi.heart.of.jessamine.common.data.service

import com.squareup.moshi.Moshi
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.data.remote.OpenRouterAiApi
import tekin.luetfi.heart.of.jessamine.common.domain.model.ChatMessage
import tekin.luetfi.heart.of.jessamine.common.domain.model.ChatRequest
import tekin.luetfi.heart.of.jessamine.common.domain.model.ResponseFormat
import tekin.luetfi.heart.of.jessamine.common.domain.service.LLMService
import tekin.luetfi.heart.of.jessamine.common.BuildConfig

class DefaultLLMService(
    private val openRouterAiApi: OpenRouterAiApi,
    private val moshi: Moshi
): LLMService {


    override suspend fun getLore(place: Place): String? {
        val messages = listOf(
            ChatMessage.systemPrompt,
            ChatMessage.userPrompt(place.context)
        )

        val request = ChatRequest(
            messages = messages,
            responseFormat = ResponseFormat("text"),
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