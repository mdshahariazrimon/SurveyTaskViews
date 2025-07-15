package com.example.surveytaskviews.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.surveytaskviews.data.model.FormResponse
import com.example.surveytaskviews.data.repository.FormRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed interface for robust UI state management
sealed interface FormUiState {
    object Loading : FormUiState
    data class Success(val formResponse: FormResponse) : FormUiState
    data class Error(val message: String) : FormUiState
}

@HiltViewModel
class FormViewModel @Inject constructor(
    private val repository: FormRepository
) : ViewModel() {

    private val _formState = MutableStateFlow<FormUiState>(FormUiState.Loading)
    val formState: StateFlow<FormUiState> = _formState

    init {
        fetchForm()
    }

    private fun fetchForm() {
        viewModelScope.launch {
            try {
                _formState.value = FormUiState.Success(repository.getForm())
            } catch (e: Exception) {
                _formState.value = FormUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}