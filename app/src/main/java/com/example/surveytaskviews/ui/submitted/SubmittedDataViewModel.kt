package com.example.surveytaskviews.ui.submitted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.surveytaskviews.data.db.SubmittedForm
import com.example.surveytaskviews.data.db.FormDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SubmittedDataViewModel @Inject constructor(
    formDao: FormDao
) : ViewModel() {

    val submittedForms: StateFlow<List<SubmittedForm>> = formDao.getAllSubmittedForms()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}