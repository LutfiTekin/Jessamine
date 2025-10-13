package tekin.luetfi.heart.of.jessamine.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter

data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage? = null
) {
    data class Choice(
        val index: Int,
        val message: ChatMessage,
        val finish_reason: String?
    )
    data class Usage(
        val prompt_tokens: Int,
        val completion_tokens: Int,
        val total_tokens: Int
    )




    fun String.cleanupCodeFences(): String {
        return removePrefix("```").removeSuffix("```").trim()
    }

    fun String.getFirstJsonObject(): String? {
        val start = indexOf('{')
        val end = lastIndexOf('}')
        return if (start >= 0 && end > start) substring(start, end + 1) else null
    }

    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified T : Any> parseResponseOrNull(moshi: Moshi): T? {
        val content = choices
            .firstOrNull { it.message.content.isNotBlank() }
            ?.message?.content
            ?: return null

        if (T::class == String::class) {
            @Suppress("UNCHECKED_CAST")
            return content as T
        }

        val json = content
            .cleanupCodeFences()
            .getFirstJsonObject()
            ?: return null

        return runCatching {
            moshi.adapter<T>().fromJson(json)
        }.getOrNull()
    }


// ───────────────────────────────────────────────────────────────────────────────
// Helpers
// ───────────────────────────────────────────────────────────────────────────────

    /** Removes surrounding ```json ... ``` or ``` ... ``` fences if present. */
    private fun String.stripCodeFences(): String {
        val trimmed = trim()
        val fenceRegex = Regex("""^```(?:json)?\s*([\s\S]*?)\s*```$""", RegexOption.IGNORE_CASE)
        val m = fenceRegex.find(trimmed)
        return if (m != null) m.groupValues[1].trim() else trimmed
    }

    /**
     * Returns the first balanced {...} block found. This is resilient to
     * accidental prose around the JSON.
     */
    private fun String.extractFirstJsonObject(): String? {
        var depth = 0
        var start = -1
        for (i in indices) {
            when (this[i]) {
                '{' -> {
                    if (depth == 0) start = i
                    depth++
                }
                '}' -> {
                    if (depth > 0) depth--
                    if (depth == 0 && start != -1) {
                        return substring(start, i + 1)
                    }
                }
            }
        }
        return null
    }

}