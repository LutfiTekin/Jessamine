package tekin.luetfi.heart.of.jessamine.data.local


data class LocationLore(val whispers: List<String> = emptyList(), val place: String? = null){
    val whispersString = whispers.joinToString("\n")
}
