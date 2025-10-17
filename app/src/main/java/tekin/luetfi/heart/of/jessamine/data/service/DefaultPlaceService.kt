package tekin.luetfi.heart.of.jessamine.data.service

import kotlinx.coroutines.ensureActive
import tekin.luetfi.heart.of.jessamine.data.model.Place
import tekin.luetfi.heart.of.jessamine.data.remote.MediaWikiApi
import tekin.luetfi.heart.of.jessamine.domain.service.PlaceService
import tekin.luetfi.heart.of.jessamine.ui.component.Confirmation
import tekin.luetfi.heart.of.jessamine.util.fallbackPlaces
import tekin.luetfi.heart.of.jessamine.util.geoSearchString
import tekin.luetfi.simple.map.data.model.Coordinates
import kotlin.coroutines.coroutineContext

class DefaultPlaceService(private val mediaWikiApi: MediaWikiApi): PlaceService {

    override suspend fun selectPlace(coordinates: Coordinates): Place {
        val geoSearchString = coordinates.geoSearchString
        //Get geo location info
        val geoQuery = try {
            mediaWikiApi.geoSearch(geoSearchCoordinates = geoSearchString, radius = 1400)
        } catch (e: Exception) {
            e.printStackTrace()
            return Place.unknown(coordinates)
        }
        coroutineContext.ensureActive()
        val geoSearchItem = try {
            geoQuery.query?.geoSearch?.random()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        val placeName = geoSearchItem?.title ?: "Unknown Place"

        val selectedCoordinates: Coordinates? = try {
            geoSearchItem ?: throw Exception()
            Coordinates(geoSearchItem.lat, geoSearchItem.lon)
        } catch (e: Exception) {
            e.printStackTrace()
            coordinates
        }

        val confirmation = Confirmation(
            finalText = placeName,
            items = geoQuery.query?.geoSearch.orEmpty()
                .map {
                    it.title
                } + fallbackPlaces)

        val place = Place(
            name = placeName,
            coordinates = selectedCoordinates,
            confirmation = confirmation
        )

        return place
    }

}