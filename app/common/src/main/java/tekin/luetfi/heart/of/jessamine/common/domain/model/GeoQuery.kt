package tekin.luetfi.heart.of.jessamine.common.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeoQuery(
    @Json(name = "geosearch") val geoSearch: List<GeoSearchItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GeoSearchItem(
    @Json(name = "pageid") val pageId: Int,
    val ns: Int,
    val title: String,
    val lat: Double,
    val lon: Double,
    @Json(name = "dist") val distance: Double,
    val primary: String? = null
)