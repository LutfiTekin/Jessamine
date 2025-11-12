package tekin.luetfi.heart.of.jessamine.common.data.model

import android.util.Base64
import tekin.luetfi.simple.map.data.model.Coordinates


data class Place(
    val name: String,
    val description: String? = null,
    val coordinates: Coordinates? = null,
    val confirmation: Confirmation = Confirmation()
){

    /**
     * Store place name in base64 encoded string to avoid special character causing a mismatch
     */
    val key: String
        get() {
            val bytes = name.toByteArray(Charsets.UTF_8)
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        }

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
        fun zero() = Place("")
    }
}