package tekin.luetfi.heart.of.jessamine.data.service

import tekin.luetfi.heart.of.jessamine.data.remote.SpeechifyApi
import tekin.luetfi.heart.of.jessamine.domain.model.SpeechRequest
import tekin.luetfi.heart.of.jessamine.domain.model.SpeechResponse
import tekin.luetfi.heart.of.jessamine.domain.service.TTSService
import tekin.luetfi.heart.of.jessamine.util.ssmlText

class DefaultTTSService(private val speechifyApi: SpeechifyApi): TTSService {
    override suspend fun synthesize(text: String): SpeechResponse {
        return speechifyApi.synthesize(SpeechRequest(text.ssmlText))
    }
}