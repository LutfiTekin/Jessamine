package tekin.luetfi.heart.of.jessamine.common.util

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import okio.buffer
import java.io.IOException

class ProgressResponseBody(
    private val responseBody: ResponseBody,
    private val onBytesAccumulated: (Long) -> Unit
) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null

    override fun contentType(): MediaType? = responseBody.contentType()
    override fun contentLength(): Long = responseBody.contentLength()

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = wrapSource(responseBody.source()).buffer()
        }
        //reset progress
        onBytesAccumulated(0)
        return bufferedSource!!
    }

    private fun wrapSource(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L

            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                if (bytesRead != -1L) {
                    totalBytesRead += bytesRead
                    onBytesAccumulated(totalBytesRead)
                }
                return bytesRead
            }
        }
    }
}
