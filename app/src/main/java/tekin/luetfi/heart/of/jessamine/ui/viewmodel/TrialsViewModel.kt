package tekin.luetfi.heart.of.jessamine.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tekin.luetfi.heart.of.jessamine.common.data.model.Place
import tekin.luetfi.heart.of.jessamine.common.data.model.Quiz
import tekin.luetfi.heart.of.jessamine.common.domain.repository.QuizRepository
import tekin.luetfi.heart.of.jessamine.ui.screen.TrialsUiState
import javax.inject.Inject

@HiltViewModel
class TrialsViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private var placesToExclude: List<String> = emptyList()


    private fun excludePlaces(places: List<String>) {
        placesToExclude = (placesToExclude + places).distinct()
    }

    val uiState: StateFlow<TrialsUiState>
        field = MutableStateFlow<TrialsUiState>(TrialsUiState.Initial)

    init {
        viewModelScope.launch {
            repository.loadedQuiz.filterNotNull().collectLatest { quiz ->
                excludePlaces(quiz.options.map { it.name })
                uiState.emit(TrialsUiState.Loaded(quiz))
            }
        }
    }

    var quizJob: Job? = null


    fun getNewQuiz(selectedPlace: suspend (Place) -> Unit) {
        viewModelScope.launch {
            uiState.emit(TrialsUiState.Loading)
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

    fun reset() = viewModelScope.launch {
        uiState.emit(TrialsUiState.Initial)
    }


    fun evaluateAnswer(place: Place) {
        val state = uiState.value
        val quiz = state.loadedQuiz
        val newState = if (state.loadedQuiz == null)
            TrialsUiState.Initial
        else if (quiz.checkAnswer(placeName = place.name))
            TrialsUiState.Correct(quiz)
        else
            TrialsUiState.Incorrect(quiz)
        viewModelScope.launch {
            uiState.emit(newState)
        }
    }


}