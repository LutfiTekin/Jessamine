package tekin.luetfi.simple.map.data.model

import java.util.Locale


data class Coordinates(
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val city: String? = null,
    val countryCode: String? = null) {

    val displayName: String
        get() {
            if (city == null) return "$lat, $lon"
            val locale = Locale.Builder().setRegion(countryCode).build()
            val countryNameInEnglish = locale.getDisplayCountry(Locale.ENGLISH)
            return "$city, $countryNameInEnglish"
        }



    companion object {
        val barcelona = Coordinates(41.38, 2.17, "Barcelona")
        val copenhagen = Coordinates(55.68, 12.59, "Copenhagen")
        val amsterdam = Coordinates(52.37, 4.90, "Amsterdam")
        val vienna = Coordinates(48.21, 16.37, "Vienna")
        val berlin = Coordinates(52.52, 13.40, "Berlin")
        val singapore = Coordinates(1.35, 103.82, "Singapore")
        val stockholm = Coordinates(59.33, 18.07, "Stockholm")
        val london = Coordinates(51.51, -0.13, "London")
        val newYork = Coordinates(40.71, -74.01, "New York")
        val tokyo = Coordinates(35.68, 139.65, "Tokyo")
        val paris = Coordinates(48.86, 2.35, "Paris")
        val madrid = Coordinates(40.42, -3.71, "Madrid")
        val antalya = Coordinates(36.89, 30.71, "Antalya")
        val istanbul = Coordinates(41.01, 28.97, "Istanbul")
        val sydney = Coordinates(-33.87, 151.21, "Sydney")
        val rome = Coordinates(41.90, 12.49, "Rome")
        val athens = Coordinates(37.99, 23.72, "Athens")
        val rioDeJaneiro = Coordinates(-22.91, -43.20, "Rio de Janeiro")


        val majorCities = listOf(
            barcelona,
            copenhagen,
            amsterdam,
            vienna,
            berlin,
            singapore,
            stockholm,
            london,
            newYork,
            tokyo,
            paris,
            madrid,
            antalya,
            istanbul,
            sydney,
            rome,
            athens,
            rioDeJaneiro
        )
    }

}


