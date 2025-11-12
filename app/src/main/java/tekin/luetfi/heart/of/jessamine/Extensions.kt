package tekin.luetfi.heart.of.jessamine

import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tekin.luetfi.heart.of.jessamine.common.util.requestLocationPermission
import tekin.luetfi.simple.map.hasLocationPermission

fun ComponentActivity.installShortcut(){
    val shortcutManager = getSystemService(ShortcutManager::class.java)

    val shortcut = ShortcutInfo.Builder(this, "quiz_mode")
        .setShortLabel("Ordeal")
        .setLongLabel("The Listener’s Trial")
        .setIcon(Icon.createWithResource(this, R.mipmap.ic_quiz_launcher))
        .setIntent(Intent(this, MainActivity::class.java).setAction(Intent.ACTION_VIEW))
        .build()

    lifecycleScope.launch(Dispatchers.IO){
        shortcutManager?.dynamicShortcuts = if (hasLocationPermission) listOf(shortcut) else emptyList()
    }
}


fun Activity.enterFullscreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Instead of setDecorFitsSystemWindows, use WindowInsetsController directly
        window.insetsController?.apply {
            hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
    }
}

fun ComponentActivity.applyFeatures(){
    requestLocationPermission()
    if (hasLocationPermission)
        enterFullscreen()
    installShortcut()
}