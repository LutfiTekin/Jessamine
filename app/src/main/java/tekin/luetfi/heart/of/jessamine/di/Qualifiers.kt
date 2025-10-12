package tekin.luetfi.heart.of.jessamine.di

import javax.inject.Qualifier

@Qualifier annotation class Speechify
@Qualifier annotation class OpenRouterAi
@Qualifier annotation class MediaWiki

@Qualifier annotation class DefaultOkHttp
@Qualifier annotation class SpeechifyOkHttp
@Qualifier annotation class OpenRouterOkHttp

@Qualifier annotation class OpenRouterAuth
@Qualifier annotation class SpeechifyAuth

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocationPermissionFlow

@Qualifier
annotation class CurrentLocationFlow