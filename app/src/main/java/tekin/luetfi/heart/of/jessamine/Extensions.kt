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

fun ComponentActivity.installShortcut() {
    val shortcutManager = getSystemService(ShortcutManager::class.java)

    val shortcutIntent = Intent(this, MainActivity::class.java).apply {
        action = getString(R.string.action_quiz_mode)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val shortcut = ShortcutInfo.Builder(this, "quiz_mode")
        .setShortLabel(getString(R.string.quiz_mode_label_short))
        .setLongLabel(getString(R.string.quiz_mode_label_long))
        .setIcon(Icon.createWithResource(this, R.mipmap.ic_quiz_launcher))
        .setIntent(shortcutIntent)
        .build()
    lifecycleScope.launch(Dispatchers.IO) {
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

val Activity.quizMode: Boolean
    get() = (intent?.action == getString(R.string.action_quiz_mode))