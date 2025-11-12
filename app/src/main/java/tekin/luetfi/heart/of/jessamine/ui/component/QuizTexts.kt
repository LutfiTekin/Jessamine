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

@Composable
fun successText(key: Int = 0): String {
    return arrayOf(
        stringResource(R.string.it_stirs_beneath),
        stringResource(R.string.the_shape_reveals),
        stringResource(R.string.you_were_not_forgotten),
        stringResource(R.string.the_pattern_accepts),
        stringResource(R.string.it_remembers_you),
        stringResource(R.string.the_gate_shifted),
        stringResource(R.string.the_mark_recognized),
        stringResource(R.string.the_thread_was_true),
        stringResource(R.string.the_eye_opened),
        stringResource(R.string.the_silence_broke)
    ).random().uppercase()
}


@Composable
fun failText(key: Int = 0): String {
    return arrayOf(
        stringResource(R.string.it_remains_buried),
        stringResource(R.string.the_shape_did_not_form),
        stringResource(R.string.you_were_not_seen),
        stringResource(R.string.the_pattern_rejected),
        stringResource(R.string.it_did_not_know_you),
        stringResource(R.string.the_gate_stayed_closed),
        stringResource(R.string.the_mark_faded),
        stringResource(R.string.the_thread_unraveled),
        stringResource(R.string.the_eye_stayed_shut),
        stringResource(R.string.the_silence_endured)
    ).random().uppercase()
}


val loadingTexts: List<String> = listOf(
    "Summoning the archive",
    "Consulting the ledger",
    "Deciphering the script",
    "Unsealing the record",
    "Transcribing the memory",
    "Conjuring the essence",
    "Surveying the relics",
    "Perusing the folio",
    "Extracting the chronicle",
    "Invoking the passage",
    "Reckoning the truth",
    "Examining the parchment",
    "Interpreting the omen",
    "Revealing the cipher",
    "Tracing the lineage",
    "Unfolding the tale",
    "Manifesting the vision",
    "Gathering the whispers",
    "Discerning the pattern",
    "Awakening the stone",
    "Consulting the oracle",
    "Rendering the likeness",
    "Binding the fragments",
    "Illuminating the veil",
    "Recollecting the echoes",
    "Assembling the lore",
    "Engaging the mechanism",
    "Revealing the sigil",
    "Surveying the chamber",
    "Summoning the shade",
    "Reading the silence",
    "Unraveling the thread",
    "Preparing the rite",
    "Opening the sanctum",
    "Disclosing the mark",
    "Interpreting the silence",
    "Reckoning the wound",
    "Divining the signs",
    "Animating the relic",
    "Evoking the memory"
)

