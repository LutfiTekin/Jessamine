package tekin.luetfi.heart.of.jessamine.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PageDescriptionResponse(
    val query: Query
) {
    data class Query(
        val pages: Map<String, Page>
    )

    data class Page(
        @Json(name = "pageid")
        val pageId: Int,
        @Json(name = "ns")
        val namespace: Int,
        @Json(name = "title")
        val title: String,
        @Json(name = "extract")
        val extract: String?
    )
}
