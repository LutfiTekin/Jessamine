package tekin.luetfi.heart.of.jessamine.common.domain.model

data class SpeechData(
    val audioData: String,
    val speechMarks: SpeechMarks?
){

    companion object{
        fun SpeechResponse.toSpeechData(): SpeechData{
            return SpeechData(
                audioData = audioData,
                speechMarks = speechMarks
            )
        }

        val SpeechData?.formattedSpeechMarks: List<Triple<Long, Long, String>>
            get() {
                return this?.speechMarks?.chunks?.mapNotNull { chunk ->
                    val startTime = (chunk["start_time"] as? Double)?.toLong() ?: return@mapNotNull null
                    val endTime = (chunk["end_time"] as? Double)?.toLong() ?: return@mapNotNull null
                    val value = chunk["value"] as? String ?: return@mapNotNull null
                    Triple(startTime, endTime, value.uppercase())
                } ?: emptyList()
            }
    }
}
