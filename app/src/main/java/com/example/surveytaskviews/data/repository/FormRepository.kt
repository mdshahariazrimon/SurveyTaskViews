package com.example.surveytaskviews.data.repository

import com.example.surveytaskviews.data.model.FormResponse
import com.example.surveytaskviews.data.network.ApiService
import javax.inject.Inject

// Implementation of the repository
class FormRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getForm(): FormResponse {
        return apiService.getForm()
    }
}