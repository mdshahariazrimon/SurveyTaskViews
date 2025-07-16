package com.example.surveytaskviews.ui.form

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.surveytaskviews.data.db.FormDao
import com.example.surveytaskviews.data.db.SubmittedForm
import com.example.surveytaskviews.data.model.Question
import com.example.surveytaskviews.data.repository.FormRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FormUiState {
    object Loading : FormUiState
    object Submitted : FormUiState
    data class Success(
        val allQuestions: List<Question>,
        val currentQuestion: Question,
        val answers: Map<String, String> = emptyMap()
    ) : FormUiState
    data class Error(val message: String) : FormUiState
}


@HiltViewModel
class FormViewModel @Inject constructor(
    private val repository: FormRepository,
    private val formDao: FormDao
) : ViewModel() {

    private val _formState = MutableStateFlow<FormUiState>(FormUiState.Loading)
    val formState: StateFlow<FormUiState> = _formState

    private var answers = mutableMapOf<String, String>()

    init {
        fetchForm()
    }

    private fun navigateToQuestion(questionId: String?) {
        val currentState = _formState.value
        if (currentState !is FormUiState.Success) return

        // NEW: Safeguard to detect and prevent navigation loops.
        if (questionId == currentState.currentQuestion.id) {
            Log.w("FormViewModel", "Navigation loop detected. Forcing submit.")
            navigateToQuestion("submit") // Treat the loop as the end of the form.
            return
        }

        if (questionId == "submit") {
            viewModelScope.launch {
                val answersJson = Gson().toJson(answers)
                formDao.insertForm(SubmittedForm(answersJson = answersJson))
                Log.d("FormViewModel", "Form submitted with answers: $answers")
                _formState.value = FormUiState.Submitted
            }
            return
        }

        val nextQuestion = currentState.allQuestions.find { it.id == questionId }
        if (nextQuestion != null) {
            _formState.update { state ->
                if (state is FormUiState.Success) {
                    state.copy(
                        currentQuestion = nextQuestion,
                        answers = answers.toMap()
                    )
                } else { state }
            }
        }
    }

    fun onNextClicked(questionId: String, answer: String) {
        val currentState = _formState.value
        if (currentState !is FormUiState.Success) return

        answers[questionId] = answer

        val currentQuestion = currentState.allQuestions.find { it.id == questionId }
        val nextQuestionId = currentQuestion?.referTo?.id

        navigateToQuestion(nextQuestionId ?: "submit")
    }

    fun onSkipClicked() {
        val currentState = _formState.value
        if (currentState !is FormUiState.Success) return

        val nextQuestionId = currentState.currentQuestion.skip.id
        navigateToQuestion(nextQuestionId)
    }

    fun startNewForm() {
        fetchForm()
    }

    private fun fetchForm() {
        answers.clear()
        _formState.value = FormUiState.Loading
        viewModelScope.launch {
            try {
                val form = repository.getForm()
                if (form.record.isNotEmpty()) {
                    _formState.value = FormUiState.Success(
                        allQuestions = form.record,
                        currentQuestion = form.record.first()
                    )
                } else {
                    _formState.value = FormUiState.Error("Form contains no questions.")
                }
            } catch (e: Exception) {
                _formState.value = FormUiState.Error(e.message ?: "An unknown error occurred")
                Log.e("FormViewModel", "Error fetching form", e)
            }
        }
    }
}