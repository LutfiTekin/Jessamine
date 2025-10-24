package tekin.luetfi.heart.of.jessamine.common.data.service

import tekin.luetfi.heart.of.jessamine.common.data.remote.SpeechifyApi
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechRequest
import tekin.luetfi.heart.of.jessamine.common.domain.model.SpeechResponse
import tekin.luetfi.heart.of.jessamine.common.domain.service.TTSService
import tekin.luetfi.heart.of.jessamine.common.util.ssmlText

class DefaultTTSService(private val speechifyApi: SpeechifyApi): TTSService {
    override suspend fun synthesize(text: String): SpeechResponse {
        return speechifyApi.synthesize(SpeechRequest(text.ssmlText))
    }
}