package com.example.main.kotlin.apartmentsAdParsers.models

data class Room(
    val id: String,
    val price: String,
    val source: String,
    var address: String,
    var lat: Double?,
    var lon: Double?,
    val url: String
) {
    val uniqueIdentity: String
        get() = "${source}_${id}"
}