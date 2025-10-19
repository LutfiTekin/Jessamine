package tekin.luetfi.heart.of.jessamine.di


import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import tekin.luetfi.heart.of.jessamine.BuildConfig
import tekin.luetfi.heart.of.jessamine.util.ByteAccumulationDispatcher
import tekin.luetfi.heart.of.jessamine.util.ProgressResponseBody
import tekin.luetfi.heart.of.jessamine.util.UserAgentInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

private const val DEFAULT_TIMEOUT = "dt"
private const val LLM_TIMEOUT = "llm_dt"


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named(DEFAULT_TIMEOUT)
    fun provideDefaultTimeOut(): Long = 3000L

    @Provides
    @Singleton
    @Named(LLM_TIMEOUT)
    fun provideLLMTimeOut(): Long = 30000L

    @Provides
    @Singleton
    fun provideLogging(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    @DefaultOkHttp
    fun provideOkHTTPClient(
        @Named(DEFAULT_TIMEOUT) defaultTimeOut: Long,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .readTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .addNetworkInterceptor(UserAgentInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    @OpenRouterOkHttp
    fun provideORAOkHTTPClient(
        @Named(LLM_TIMEOUT) defaultTimeOut: Long,
        loggingInterceptor: HttpLoggingInterceptor,
        @OpenRouterAuth authInterceptor: Interceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .readTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    @SpeechifyOkHttp
    fun provideSpeechifyOkHTTPClient(
        @Named(LLM_TIMEOUT) defaultTimeOut: Long,
        loggingInterceptor: HttpLoggingInterceptor,
        @SpeechifyAuth authInterceptor: Interceptor,
        downloadProgressInterceptor: DownloadProgressInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .readTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .addNetworkInterceptor(UserAgentInterceptor())
            .addNetworkInterceptor(downloadProgressInterceptor)
            .addInterceptor(authInterceptor)
            //.addInterceptor(loggingInterceptor)
            .build()

    class DownloadProgressInterceptor(
        private val byteDispatcher: ByteAccumulationDispatcher
    ) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val originalResponse = chain.proceed(chain.request())
            val body = originalResponse.body
            return originalResponse.newBuilder()
                .body(ProgressResponseBody(body) { bytesRead ->
                    byteDispatcher.update(bytesRead)
                })
                .build()
        }
    }

    @Provides
    @Singleton
    fun provideDownloadProgressInterceptor(
        dispatcher: ByteAccumulationDispatcher
    ): DownloadProgressInterceptor = DownloadProgressInterceptor(dispatcher)


    @Provides
    @Singleton
    @OpenRouterAuth
    fun provideOpenRouterAuthInterceptor(@ApplicationContext context: Context): Interceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.OPENROUTERAI_API_KEY}")
            //Optional but recommended by OpenRouter
            .header("HTTP-Referer", "https://" + context.packageName)
            .header("X-Title", BuildConfig.OPEN_ROUTER_APP_NAME)

        chain.proceed(builder.build())
    }

    @Provides
    @Singleton
    @SpeechifyAuth
    fun provideSpeechifyAuthInterceptor(@ApplicationContext context: Context): Interceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.SPEECHIFY_API_KEY}")
            //Optional but recommended by OpenRouter
            .header("HTTP-Referer", context.packageName)
            .header("X-Title", BuildConfig.OPEN_ROUTER_APP_NAME)

        chain.proceed(builder.build())
    }

    @Provides
    @Singleton
    fun providesMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun providesMoshiConverter(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    private fun retrofit(
        baseUrl: String,
        client: OkHttpClient,
        moshiConverter: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(moshiConverter)
            .build()
    }


    @Provides
    @Singleton
    @Speechify
    fun provideSpeechifyRetrofit(
        @SpeechifyOkHttp okHttpClient: OkHttpClient, moshiConverter: MoshiConverterFactory
    ): Retrofit = retrofit(BuildConfig.SPEECHIFY_API, okHttpClient, moshiConverter)

    @Provides
    @Singleton
    @OpenRouterAi
    fun provideRetrofitORA(
        @OpenRouterOkHttp okHttpClient: OkHttpClient, moshiConverter: MoshiConverterFactory
    ): Retrofit = retrofit(BuildConfig.OPENROUTERAI_API, okHttpClient, moshiConverter)

    @Provides
    @Singleton
    @MediaWiki
    fun provideMediaWikiApi(
        @DefaultOkHttp okHttpClient: OkHttpClient, moshiConverter: MoshiConverterFactory
    ): Retrofit = retrofit(BuildConfig.WIKIPEDIA_API, okHttpClient, moshiConverter)

}