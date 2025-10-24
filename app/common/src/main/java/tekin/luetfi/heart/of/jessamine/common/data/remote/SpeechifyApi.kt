package tekin.luetfi.heart.of.jessamine.common.data.remote

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechRequest
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechResponse


interface SpeechifyApi {
    @Headers("Content-Type: application/json")
    @POST("v1/audio/speech")
    suspend fun synthesize(@Body request: SpeechRequest): SpeechResponse
}