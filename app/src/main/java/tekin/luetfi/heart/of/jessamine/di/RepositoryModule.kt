package tekin.luetfi.heart.of.jessamine.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import tekin.luetfi.heart.of.jessamine.data.remote.MediaWikiApi
import tekin.luetfi.heart.of.jessamine.data.remote.OpenRouterAiApi
import tekin.luetfi.heart.of.jessamine.data.remote.SpeechifyApi
import tekin.luetfi.heart.of.jessamine.data.repository.DefaultLocationInfoRepository
import tekin.luetfi.heart.of.jessamine.domain.repository.LocationInfoRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideOpenRouterApi(@OpenRouterAi retrofit: Retrofit): OpenRouterAiApi =
        retrofit.create(OpenRouterAiApi::class.java)

    @Provides
    @Singleton
    fun provideSpeechifyApi(@Speechify retrofit: Retrofit): SpeechifyApi =
        retrofit.create(SpeechifyApi::class.java)

    @Provides
    @Singleton
    fun provideMediaWikiApi(@MediaWiki retrofit: Retrofit): MediaWikiApi =
        retrofit.create(MediaWikiApi::class.java)


    @Provides
    @Singleton
    fun provideDefaultLocationInfoRepository(
        openRouterApi: OpenRouterAiApi,
        speechifyApi: SpeechifyApi,
        mediaWikiApi: MediaWikiApi,
        moshi: Moshi,
    ): LocationInfoRepository =
        DefaultLocationInfoRepository(openRouterApi, speechifyApi, mediaWikiApi, moshi)



}