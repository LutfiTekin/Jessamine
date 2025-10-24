package tekin.luetfi.heart.of.jessamine.common.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ByteAccumulationDispatcher @Inject constructor() {
    private val _bytesAccumulated = MutableStateFlow(0L)
    val bytesAccumulated: StateFlow<Long> = _bytesAccumulated.asStateFlow()

    fun update(bytesRead: Long) {
        _bytesAccumulated.value = bytesRead
    }
}
