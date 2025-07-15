package com.example.surveytaskviews.ui.form

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.surveytaskviews.data.model.Question
import com.example.surveytaskviews.data.repository.FormRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FormUiState {
    object Loading : FormUiState
    data class Success(
        val allQuestions: List<Question>,
        val currentQuestion: Question,
        val answers: Map<String, String> = emptyMap()
    ) : FormUiState
    data class Error(val message: String) : FormUiState
}

@HiltViewModel
class FormViewModel @Inject constructor(
    private val repository: FormRepository
) : ViewModel() {

    private val _formState = MutableStateFlow<FormUiState>(FormUiState.Loading)
    val formState: StateFlow<FormUiState> = _formState

    private val answers = mutableMapOf<String, String>()

    init {
        fetchForm()
    }

    fun onNextClicked(answer: String) {
        val currentState = _formState.value
        if (currentState !is FormUiState.Success) return

        answers[currentState.currentQuestion.id] = answer
        val nextQuestionId = currentState.currentQuestion.referTo.id
        val nextQuestion = currentState.allQuestions.find { it.id == nextQuestionId }

        if (nextQuestion != null) {
            _formState.update {
                (it as FormUiState.Success).copy(
                    currentQuestion = nextQuestion,
                    answers = answers.toMap()
                )
            }
        } else {
            // Handle submit logic later
        }
    }

    private fun fetchForm() {
        viewModelScope.launch {
            try {
                val form = repository.getForm()
                Log.d("FormViewModel", "Successfully fetched form with ${form.record.size} questions.")
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