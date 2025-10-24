package tekin.luetfi.heart.of.jessamine.wear.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.CurvedAlignment
import androidx.wear.compose.foundation.CurvedDirection
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.curvedRow
import androidx.wear.compose.material.curvedText
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.wear.util.isRoundDevice

@Composable
fun CurvedPlaceLabel(place: Place?){
    val placeName = place?.name ?: return
    val color = MaterialTheme.colorScheme.tertiary
    if (isRoundDevice()) {
        CurvedLayout(
            anchor = 90f,
            radialAlignment = CurvedAlignment.Radial.Inner,
            angularDirection = CurvedDirection.Angular.Reversed
        ) {
            curvedRow {
                curvedText(
                    text = placeName.uppercase(),
                    style = CurvedTextStyle(
                        fontSize = 12.sp,
                        color = color
                    )
                )
            }
        }
    }
}