package tekin.luetfi.heart.of.jessamine.data.model

import tekin.luetfi.heart.of.jessamine.ui.component.Confirmation
import tekin.luetfi.simple.map.data.model.Coordinates

data class Place(
    val name: String,
    val coordinates: Coordinates?,
    val confirmation: Confirmation = Confirmation()
){
    companion object{
        fun unknown(coordinates: Coordinates) = Place("Unknown", coordinates)
    }
}