package tekin.luetfi.heart.of.jessamine.domain.service

interface LLMService {
    suspend fun getLore(placeName: String): String?
}