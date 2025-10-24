package tekin.luetfi.heart.of.jessamine.common.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import tekin.luetfi.heart.of.jessamine.common.R

@Composable
fun initialScreenText(key: Int = 0): String {
    return arrayOf(
        stringResource(R.string.unseal_the_truth),
        stringResource(R.string.the_stone_remembers),
        stringResource(R.string.let_the_rot_speak),
        stringResource(R.string.hear_the_echoes),
        stringResource(R.string.open_my_eye),
        stringResource(R.string.the_sin_is_deep),
        stringResource(R.string.the_secrets_await),
        stringResource(R.string.let_the_silence_break),
        stringResource(R.string.the_walls_bleed_memory),
        stringResource(R.string.draw_the_story_out)
    ).random().uppercase()
}