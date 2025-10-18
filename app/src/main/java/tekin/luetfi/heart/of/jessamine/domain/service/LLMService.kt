package tekin.luetfi.heart.of.jessamine.domain.service

import tekin.luetfi.heart.of.jessamine.data.model.Place

interface LLMService {
    suspend fun getLore(place: Place): String?
}