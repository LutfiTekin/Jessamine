package tekin.luetfi.heart.of.jessamine.domain.service

import tekin.luetfi.heart.of.jessamine.data.model.Place
import tekin.luetfi.simple.map.data.model.Coordinates

interface PlaceService {
    suspend fun selectPlace(coordinates: Coordinates): Place
}