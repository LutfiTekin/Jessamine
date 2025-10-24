package tekin.luetfi.heart.of.jessamine.common.domain.service

import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechResponse

interface TTSService {

    suspend fun synthesize(text: String): SpeechResponse

}