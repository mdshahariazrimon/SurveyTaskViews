package com.example.surveytaskviews.data.model

import com.google.gson.annotations.SerializedName

// New data class for the referTo object
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
    @SerializedName("label")
    val label: String,
    @SerializedName("options")
    val options: List<Option>?,
    // Updated to use the ReferTo object
    @SerializedName("referTo")
    val referTo: ReferTo,
    @SerializedName("skip")
    val skip: Skip,
    @SerializedName("regex")
    val regex: String?
)