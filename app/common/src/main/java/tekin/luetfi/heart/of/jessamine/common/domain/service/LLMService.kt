package tekin.luetfi.heart.of.jessamine.common.domain.service

import tekin.luetfi.heart.of.jessamine.common.data.model.Place

interface LLMService {

    suspend fun getLore(place: Place): String?

}