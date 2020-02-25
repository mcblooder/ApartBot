package com.example.fetchingStrategies.neagent

import com.google.gson.annotations.SerializedName

data class FeatureCollection (
    val features: List<Feature>
)

data class Feature (
    val geometry: Geometry,
    val properties: Properties
)

data class Geometry (
    val coordinates: List<String>
)

data class Properties (
    val id: String,
    val city: String,
    val title: String,
    val date: String,

    @SerializedName("name_object")
    val nameObject: String,
    val price: String,
    val street: String
)
