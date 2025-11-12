package tekin.luetfi.heart.of.jessamine.common.data.model

data class Quiz(
    val correctPlace: Place,
    val options: List<Place>
){

    fun checkAnswer(placeName: String): Boolean{
        return placeName == correctPlace.name
    }


}