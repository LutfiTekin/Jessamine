package tekin.luetfi.heart.of.jessamine.common.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import tekin.luetfi.heart.of.jessamine.common.data.model.Confirmation
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.data.model.Quiz
import tekin.luetfi.heart.of.jessamine.common.domain.repository.QuizRepository
import tekin.luetfi.heart.of.jessamine.common.domain.service.LLMService

class DefaultQuizRepository(private val llmService: LLMService) : QuizRepository {

    private val _quiz = MutableStateFlow<Quiz?>(null)

    override val loadedQuiz: Flow<Quiz?>
        get() = _quiz

    override suspend fun getQuiz(excludedPlaces: List<String>): Place {
        val loadedPlaces = llmService.getPlaces(excludedPlaces)
        val selectedPlaces = loadedPlaces
            .shuffled()
            .take(4).map { place ->
                val confirmation = Confirmation(
                    finalText = place.name,
                    items = loadedPlaces.shuffled().map { it.name })
                place.copy(confirmation = confirmation)
            }
        val selectedPlace = selectedPlaces.random()
        val quiz = Quiz(
            options = selectedPlaces,
            correctPlace = selectedPlace
        )
        _quiz.emit(quiz)
        return selectedPlace
    }

}