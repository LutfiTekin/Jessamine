package tekin.luetfi.heart.of.jessamine.di

import javax.inject.Qualifier

@Qualifier annotation class SpeechifyApi
@Qualifier annotation class OpenRouterApi

@Qualifier annotation class SpeechifyOkHttp
@Qualifier annotation class OpenRouterOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocationPermissionFlow