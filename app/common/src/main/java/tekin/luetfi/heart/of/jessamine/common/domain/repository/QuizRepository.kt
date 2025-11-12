package tekin.luetfi.heart.of.jessamine.common.domain.repository

import kotlinx.coroutines.flow.Flow
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.data.model.Quiz

interface QuizRepository {

    val loadedQuiz: Flow<Quiz?>

    suspend fun getQuiz(excludedPlaces: List<String>): Place

}