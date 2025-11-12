package tekin.luetfi.heart.of.jessamine.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import tekin.luetfi.heart.of.jessamine.R

@Composable
fun initialQuizScreenText(key: Int = 0): String {
    return arrayOf(
        stringResource(R.string.trace_the_forgotten_lines),
        stringResource(R.string.sift_through_the_ashes),
        stringResource(R.string.the_past_whispers_back),
        stringResource(R.string.unlock_the_shrouded_truth),
        stringResource(R.string.follow_the_cracks),
        stringResource(R.string.the_dust_knows_all),
        stringResource(R.string.dig_where_the_light_fades),
        stringResource(R.string.the_echo_never_lies),
        stringResource(R.string.read_the_wounds),
        stringResource(R.string.the_truth_bleeds_through)
    ).random().uppercase()
}
