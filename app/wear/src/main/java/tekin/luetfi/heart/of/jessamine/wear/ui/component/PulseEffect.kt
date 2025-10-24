package tekin.luetfi.heart.of.jessamine.wear.ui.component

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun PulseEffect(content: @Composable (Modifier) -> Unit){
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    var bpm by remember { mutableIntStateOf(0) }

    val heartRateListener = rememberHeartRateListener { bpm = it }

    val pulse = remember { Animatable(1f) }

    LaunchedEffect(bpm) {
        if (bpm == 0)
            return@LaunchedEffect
        val duration = (60_000 / bpm).coerceIn(400, 2000) // Clamp to avoid erratic animation
        while (true) {
            pulse.animateTo(1.2f, animationSpec = tween(duration / 2))
            pulse.animateTo(1f, animationSpec = tween(duration / 2))
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(
            heartRateListener,
            heartRateSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        onDispose {
            sensorManager.unregisterListener(heartRateListener)
        }
    }

   content(Modifier.scale(pulse.value))

}



@Composable
fun rememberHeartRateListener(onHeartRateChanged: (Int) -> Unit): SensorEventListener {
    return remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val bpm = event?.values?.firstOrNull()?.toInt() ?: return
                onHeartRateChanged(bpm)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }
}
