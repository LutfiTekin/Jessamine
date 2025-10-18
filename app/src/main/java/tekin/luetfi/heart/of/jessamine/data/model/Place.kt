package tekin.luetfi.heart.of.jessamine.data.model

import tekin.luetfi.heart.of.jessamine.ui.component.Confirmation
import tekin.luetfi.simple.map.data.model.Coordinates




data class Place(
    val name: String,
    val description: String? = null,
    val coordinates: Coordinates? = null,
    val confirmation: Confirmation = Confirmation()
){

    val context: String
        get() {
            return if (description == null)
                name
            else
                buildString {
                    append(name)
                    append(description)
                }
        }

    companion object{
        const val UNKNOWN = "Unknown Place"
        fun unknown(coordinates: Coordinates) = Place(UNKNOWN, coordinates = coordinates)
    }
}