package tekin.luetfi.heart.of.jessamine.wear.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.curvedRow
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.curvedText
import tekin.luetfi.heart.of.jessamine.common.ui.component.locationWarningText

@Composable
fun LocationWarningCurved() {
    val text = locationWarningText()
    if (isRoundDevice()) {
        CurvedLayout {
            curvedRow {
                curvedText(
                    text = text,
                    style = CurvedTextStyle(
                        fontSize = 12.sp,
                        color = Color.Red
                    )
                )
            }
        }
    } else {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,
            color = Color.Red
        )
    }
}

@Composable
fun isRoundDevice(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.isScreenRound
}