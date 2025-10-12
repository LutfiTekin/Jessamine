package tekin.luetfi.heart.of.jessamine.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionMonitor @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val _isGranted = MutableStateFlow(checkPermission())
    val isGranted: StateFlow<Boolean> = _isGranted

    fun refresh() { _isGranted.value = checkPermission() }

    private fun checkPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
}