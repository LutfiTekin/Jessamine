package tekin.luetfi.heart.of.jessamine.common.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import tekin.luetfi.simple.map.currentLocation
import tekin.luetfi.simple.map.data.model.Coordinates
import tekin.luetfi.simple.map.hasLocationPermission
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)


    @Provides
    @Singleton
    @LocationPermissionFlow
    fun providePermissionState(
        @ApplicationContext app: Context
    ): MutableStateFlow<Boolean> = MutableStateFlow(app.hasLocationPermission)


    // The shared, refreshable coordinates stream
    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    @Singleton
    @CurrentLocationFlow
    fun provideLocationFlow(
        @ApplicationScope scope: CoroutineScope,
        @ApplicationContext app: Context,
        @LocationPermissionFlow permissionFlow: MutableStateFlow<Boolean>
    ): StateFlow<Coordinates> {
        val gated = permissionFlow
            .flatMapLatest { granted ->
                println("Granted: $granted")
                if (granted) app.currentLocation() else flowOf(Coordinates.Companion.majorCities.random())
            }
        return gated.stateIn(
            scope = scope,
            started = SharingStarted.Companion.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = Coordinates.Companion.majorCities.random()
        )
    }

}