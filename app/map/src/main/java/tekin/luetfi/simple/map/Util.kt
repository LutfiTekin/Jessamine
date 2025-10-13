package tekin.luetfi.simple.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import tekin.luetfi.simple.map.data.model.Coordinates
import kotlinx.coroutines.tasks.await

val Context.hasLocationPermission: Boolean
    get() = ContextCompat
        .checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

val Context.hasCoarseLocationPermission: Boolean
    get() = ContextCompat
        .checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
suspend fun Context.getUserLocationLastKnown(): Coordinates? {
    val fused = LocationServices.getFusedLocationProviderClient(this)
    val location = try {
        fused.lastLocation.await() // May be null
    } catch (e: Exception) {
        null
    }
    return location?.let { Coordinates(it.latitude, it.longitude) }
}

@OptIn(ExperimentalCoroutinesApi::class)
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
suspend fun Context.getUserLocationCurrent(
    priority: Int = Priority.PRIORITY_BALANCED_POWER_ACCURACY
): Coordinates? {
    val fused = LocationServices.getFusedLocationProviderClient(this)
    val cts = CancellationTokenSource()
    return try {
        val location = fused.getCurrentLocation(priority, cts.token).await(cts)
        location?.let { Coordinates(it.latitude, it.longitude) }
    } finally {
        cts.cancel()
    }
}


@SuppressLint("MissingPermission")
fun Context.currentLocation(
    intervalMillis: Long = 10_000L,
    minUpdateIntervalMillis: Long = 52_000L,
    minUpdateDistanceMeters: Float = 0f,
    priority: Int = Priority.PRIORITY_HIGH_ACCURACY
): Flow<Coordinates> = callbackFlow {

    if (hasLocationPermission.not()){
        trySend(Coordinates.majorCities.random())
        close()
        return@callbackFlow
    }

    val fused = LocationServices.getFusedLocationProviderClient(this@currentLocation)

    fused.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            trySend(Coordinates(location.latitude, location.longitude))
        }
    }

    val request = LocationRequest.Builder(intervalMillis)
        .setPriority(priority)
        .setMinUpdateIntervalMillis(minUpdateIntervalMillis)
        .setMinUpdateDistanceMeters(minUpdateDistanceMeters)
        .build()

    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            for (loc in result.locations) {
                trySend(Coordinates(loc.latitude, loc.longitude)).isSuccess
            }
        }
    }

    fused.requestLocationUpdates(request, callback, Looper.getMainLooper())
    awaitClose { fused.removeLocationUpdates(callback) }
}.conflate()