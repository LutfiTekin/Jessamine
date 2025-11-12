package tekin.luetfi.heart.of.jessamine.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import tekin.luetfi.heart.of.jessamine.common.data.remote.MediaWikiApi
import tekin.luetfi.heart.of.jessamine.common.data.remote.OpenRouterAiApi
import tekin.luetfi.heart.of.jessamine.common.data.remote.SpeechifyApi
import tekin.luetfi.heart.of.jessamine.common.data.repository.DefaultLocationInfoRepository
import tekin.luetfi.heart.of.jessamine.common.data.repository.DefaultQuizRepository
import tekin.luetfi.heart.of.jessamine.common.domain.repository.LocationInfoRepository
import tekin.luetfi.heart.of.jessamine.common.domain.repository.QuizRepository
import tekin.luetfi.heart.of.jessamine.common.domain.service.LLMService
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
    fun provideDefaultLocationInfoRepository(): LocationInfoRepository =
        DefaultLocationInfoRepository()


    @Provides
    @Singleton
    fun provideDefaultQuizRepository(llmService: LLMService): QuizRepository =
        DefaultQuizRepository(llmService = llmService)

}