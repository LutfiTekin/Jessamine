package tekin.luetfi.heart.of.jessamine.common.util


import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityMonitor @Inject constructor(
    permissionMonitor: PermissionMonitor,
    networkMonitor: NetworkMonitor
) {
    val isConnected: StateFlow<Boolean> = combine(
        permissionMonitor.isGranted,
        networkMonitor.isOnline
    ) { hasLocationPermission, isOnline ->
        hasLocationPermission && isOnline
    }.stateIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
}
