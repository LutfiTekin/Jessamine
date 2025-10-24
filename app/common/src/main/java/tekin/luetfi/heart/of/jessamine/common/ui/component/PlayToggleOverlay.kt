package tekin.luetfi.heart.of.jessamine.common.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun PlayToggleOverlay(
    initialState: Boolean,
    onStart: () -> Unit,
    onReset: () -> Unit,
    onFinish: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    if (zoom < 1f) { // Zoom-out gesture
                        onFinish()
                    }
                }
            }
            .clickable(
                indication = androidx.compose.foundation.LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() },
            ) {
                if (initialState) {
                    onStart()
                } else {
                    onReset()
                }
            }
    )
}
