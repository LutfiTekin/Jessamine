package tekin.luetfi.heart.of.jessamine.common.domain.service

import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.simple.map.data.model.Coordinates

interface PlaceService {
    suspend fun selectPlace(coordinates: Coordinates): Place
}