package tekin.luetfi.heart.of.jessamine.util

import okhttp3.Interceptor
import okhttp3.Response
import tekin.luetfi.heart.of.jessamine.BuildConfig
import tekin.luetfi.simple.map.data.model.Coordinates

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