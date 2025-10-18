package tekin.luetfi.heart.of.jessamine.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import tekin.luetfi.heart.of.jessamine.domain.model.GeoSearchResponse
import tekin.luetfi.heart.of.jessamine.domain.model.PageDescriptionResponse
import tekin.luetfi.simple.map.data.model.Coordinates

interface MediaWikiApi {
    @GET("w/api.php")
    suspend fun geoSearch(
        @Query("action") action: String = "query",
        @Query("list") list: String = "geosearch",
        @Query("gscoord") geoSearchCoordinates: String,     // "lat|lon", e.g., "52.5163|13.3777"
        @Query("gsradius") radius: Int? = null, // meters, optional
        @Query("gslimit") limit: Int? = null,   // max results, optional
        @Query("format") format: String = "json"
    ): GeoSearchResponse

    @GET("w/api.php")
    suspend fun getPageDescription(
        @Query("action") action: String = "query",
        @Query("titles") titles: String,
        @Query("prop") properties: String = "extracts",
        @Query("exintro") introOnly: Int = 1,
        @Query("explaintext") plainText: Int = 1,
        @Query("exchars") maxChars: Int? = 500,
        @Query("format") format: String = "json"
    ): PageDescriptionResponse

}