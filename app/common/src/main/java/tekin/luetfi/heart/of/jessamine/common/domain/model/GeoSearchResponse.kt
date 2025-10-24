package tekin.luetfi.heart.of.jessamine.common.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeoSearchResponse(
    @field:Json(name = "batchcomplete") val batchComplete: String? = null,
    val query: GeoQuery? = null
)
