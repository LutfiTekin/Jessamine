package tekin.luetfi.heart.of.jessamine.common.data.model

data class Confirmation(val finalText: String = "", private val items: List<String> = emptyList()){
    val list: List<String>
        get() = (items + listOf(finalText)).toSet().shuffled().toList()
}