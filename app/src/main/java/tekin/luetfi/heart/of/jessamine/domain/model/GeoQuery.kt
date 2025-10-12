package tekin.luetfi.heart.of.jessamine.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeoQuery(
    @field:Json(name = "geosearch") val geoSearch: List<GeoSearchItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GeoSearchItem(
    @field:Json(name = "pageid") val pageId: Int,
    val ns: Int,
    val title: String,
    val lat: Double,
    val lon: Double,
    @field:Json(name = "dist") val distance: Double,
    val primary: String? = null
)