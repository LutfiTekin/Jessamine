package tekin.luetfi.heart.of.jessamine.data.local


data class LocationLore(val whispers: List<String> = emptyList()){
    val whispersString = whispers.joinToString("\n")
}
