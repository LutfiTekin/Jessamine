package tekin.luetfi.heart.of.jessamine.common.data.remote

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import tekin.luetfi.heart.of.jessamine.common.domain.model.ChatCompletionResponse
import tekin.luetfi.heart.of.jessamine.common.domain.model.ChatRequest

interface OpenRouterAiApi {

    @POST("chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun getChatCompletion(
        @Body body: ChatRequest
    ): ChatCompletionResponse
}