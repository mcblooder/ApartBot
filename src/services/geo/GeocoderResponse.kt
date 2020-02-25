package com.example.services.geo;

import com.google.gson.annotations.SerializedName

data class GeocoderResponse (
    val city: String?,

    @SerializedName("full_text")
    val fullText: String,

    @SerializedName("short_text")
    val shortText: String,

    val description: String?,
    val point: List<Double>?,
    val type: String,

    @SerializedName("object_type")
    val objectType: String,

    val exact: Boolean
)

data class GeoEncoderResponse (
    val objects: List<GeoObject>
)

data class GeoObject (
    val city: String?,
    val country: String?,
    val description: String?,
    val exact: Boolean,

    @SerializedName("full_text")
    val fullText: String,

    val house: String?,
    val point: List<Double>,

    @SerializedName("short_text")
    val shortText: String,

    val street: String?,
    val type: String
)
