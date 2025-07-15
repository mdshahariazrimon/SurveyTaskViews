package com.example.surveytaskviews.data.network

import com.example.surveytaskviews.data.model.FormResponse
import retrofit2.http.GET

interface ApiService {
    @GET("b/687374506063391d31aca23a")
    suspend fun getForm(): FormResponse
}