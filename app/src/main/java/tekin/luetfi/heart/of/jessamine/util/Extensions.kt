package tekin.luetfi.heart.of.jessamine.util

import okhttp3.Interceptor
import okhttp3.Response
import tekin.luetfi.heart.of.jessamine.BuildConfig
import tekin.luetfi.simple.map.data.model.Coordinates

const val PLACE = "place"
const val SPEECH_MARKS = "speechMarks"

val Coordinates.geoSearchString: String
    get() = "$lat|$lon"


class UserAgentInterceptor(private val userAgent: String = BuildConfig.APP_USER_AGENT) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithUserAgent = originalRequest.newBuilder()
            .removeHeader("User-Agent") // Remove default OkHttp User-Agent
            .addHeader("User-Agent", userAgent)
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}

fun Float.isCloseTo(target: Float, tolerance: Float = 0.01f): Boolean {
    return kotlin.math.abs(this - target) < tolerance
}

val Long.beatDurationMillis: Int
    get() {
        val maxBytes = 2_097_152L // 2MB
        val stepCount = 4
        val stepSize = 180 // ms per step → 800 → 200

        val progress = (this.toFloat() / maxBytes).coerceIn(0f, 1f)
        val stepIndex = (progress * stepCount).toInt().coerceIn(0, stepCount - 1)

        return 800 - (stepIndex * stepSize)
    }


val String.ssmlText: String
    get() {
        val lines = split("\n") // Split lore into lines
        return buildString {
            append("<speak>")
            append("<speechify:style emotion=\"fearful\">")
            lines.forEachIndexed { index, line ->
                if (index == lines.size - 1){
                    append("<break time=\"1500ms\"/>")
                }else if (index > 0) {
                    append("<break time=\"250ms\"/>")
                }
                append(line.trim()) // Append trimmed line content
            }
            append("</speechify:style>")
            append("</speak>")
        }
    }

val fallbackPlaces = listOf(
    "Old Watchtower",
    "Stone Bridge",
    "North Windmill",
    "Abandoned Depot",
    "Hilltop Chapel",
    "Abandoned Station",
    "South Pier",
    "The Iron Gate",
    "Cobbler’s Alley",
    "The Weathered Arch",
    "Foggy Crossing",
    "The Broken Stair",
    "East Reservoir",
    "The Quiet Courtyard",
    "Sunken Garden",
    "The Rusted Rail",
    "West Bell Tower",
    "The Forgotten Tunnel"
)

