package tekin.luetfi.heart.of.jessamine.common.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import tekin.luetfi.heart.of.jessamine.common.R

@Composable
fun locationWarningText(): String {
    return arrayOf(
        stringResource(R.string.location_denied_1),
        stringResource(R.string.location_denied_2),
        stringResource(R.string.location_denied_3),
        stringResource(R.string.location_denied_4),
        stringResource(R.string.location_denied_5),
        stringResource(R.string.location_denied_6)
    ).random()
}