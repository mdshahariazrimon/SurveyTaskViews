package com.example.surveytaskviews.data.model

import com.google.gson.annotations.SerializedName

data class FormResponse(
    @SerializedName("record")
    val record: List<Question>
)

data class Question(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("label")
    val label: String,
    @SerializedName("options")
    val options: List<String>?,
    @SerializedName("referTo")
    val referTo: String,
    @SerializedName("skip")
    val skip: String,
    @SerializedName("regex")
    val regex: String?
)