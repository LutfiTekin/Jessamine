package tekin.luetfi.heart.of.jessamine.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tekin.luetfi.heart.of.jessamine.data.remote.MediaWikiApi
import tekin.luetfi.heart.of.jessamine.data.remote.OpenRouterAiApi
import tekin.luetfi.heart.of.jessamine.data.remote.SpeechifyApi
import tekin.luetfi.heart.of.jessamine.data.service.DefaultLLMService
import tekin.luetfi.heart.of.jessamine.data.service.DefaultPlaceService
import tekin.luetfi.heart.of.jessamine.data.service.DefaultTTSService
import tekin.luetfi.heart.of.jessamine.domain.service.LLMService
import tekin.luetfi.heart.of.jessamine.domain.service.PlaceService
import tekin.luetfi.heart.of.jessamine.domain.service.TTSService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideLLMService(openRouterAiApi: OpenRouterAiApi, moshi: Moshi): LLMService =
        DefaultLLMService(openRouterAiApi, moshi)

    @Provides
    @Singleton
    fun providePlaceService(mediaWikiApi: MediaWikiApi): PlaceService =
        DefaultPlaceService(mediaWikiApi)

    @Provides
    @Singleton
    fun provideTTSService(speechifyApi: SpeechifyApi): TTSService =
        DefaultTTSService(speechifyApi)

}