package tekin.luetfi.heart.of.jessamine.domain.service

import tekin.luetfi.heart.of.jessamine.domain.model.SpeechResponse

interface TTSService {

    suspend fun synthesize(text: String): SpeechResponse

}