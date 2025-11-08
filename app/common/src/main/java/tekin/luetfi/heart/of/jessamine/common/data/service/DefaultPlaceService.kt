package tekin.luetfi.heart.of.jessamine.common.data.service

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import tekin.luetfi.heart.of.jessamine.common.data.model.Confirmation
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.data.remote.MediaWikiApi
import tekin.luetfi.heart.of.jessamine.common.domain.service.PlaceService
import tekin.luetfi.heart.of.jessamine.common.util.fallbackPlaces
import tekin.luetfi.heart.of.jessamine.common.util.geoSearchString
import tekin.luetfi.simple.map.data.model.Coordinates
import kotlin.collections.orEmpty
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
        currentCoroutineContext().ensureActive()
        val geoSearchItem = try {
            geoQuery.query?.geoSearch?.random()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        val placeName = geoSearchItem?.title ?: Place.UNKNOWN

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

        val description = getMoreContextAbout(place)
        coroutineContext.ensureActive()
        return place.copy(description = description)
    }


    private suspend fun getMoreContextAbout(place: Place): String? {
        if (place.name == Place.UNKNOWN) {
            return null
        }
        val description = try {
            mediaWikiApi
                .getPageDescription(titles = place.name)
                .query
                .pages
                .entries
                .first()
                .value
                .extract
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }


        return buildString {
            appendLine()
            appendLine("More Context:")
            append(description)
        }
    }

}