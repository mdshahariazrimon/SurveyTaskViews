package com.example.surveytaskviews.data.model

import com.google.gson.annotations.SerializedName

data class ReferTo(
    @SerializedName("id")
    val id: String
)

data class Option(
    @SerializedName("value")
    val value: String
)

data class Skip(
    @SerializedName("id")
    val id: String
)

data class FormResponse(
    @SerializedName("record")
    val record: List<Question>
)

data class Question(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    // MODIFIED: Made label nullable
    @SerializedName("label")
    val label: String?,
    @SerializedName("options")
    val options: List<Option>?,
    // MODIFIED: Made referTo nullable
    @SerializedName("referTo")
    val referTo: ReferTo?,
    @SerializedName("skip")
    val skip: Skip,
    @SerializedName("regex")
    val regex: String?
)