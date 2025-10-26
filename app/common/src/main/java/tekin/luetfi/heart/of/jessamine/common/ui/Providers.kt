package tekin.luetfi.heart.of.jessamine.common.ui

import androidx.compose.runtime.staticCompositionLocalOf
import tekin.luetfi.heart.of.jessamine.common.util.ConnectivityMonitor

val LocalConnectivityMonitor = staticCompositionLocalOf<ConnectivityMonitor> {
    error("ConnectivityMonitor not provided")
}