package tekin.luetfi.heart.of.jessamine.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.domain.repository.QuizRepository
import javax.inject.Inject

@HiltViewModel
class TrialsViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private var placesToExclude: List<String> = emptyList()


    private fun excludePlaces(places: List<String>) {
        placesToExclude = (placesToExclude + places).distinct()
    }

    val quiz = repository.loadedQuiz
        .onEach { quiz ->
            if (quiz != null) {
                excludePlaces(quiz.options.map { it.name })
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null)

    var quizJob: Job? = null


    fun getNewQuiz(selectedPlace: suspend (Place) -> Unit) {
        viewModelScope.launch {
            quizJob?.cancelAndJoin()
            quizJob = launch(Dispatchers.IO) {
                runCatching {
                    repository.getQuiz(placesToExclude)
                }.onSuccess { place ->
                    selectedPlace(place)
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }


}