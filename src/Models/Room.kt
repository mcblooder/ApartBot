package com.example.Models

data class Room(
    val id: String,
    val price: String,
    val source: String,
    val address: String,
    var lat: Double?,
    var lon: Double?,
    val url: String
) {
    val uniqueIdentity: String
        get() = source + "_" + id
}