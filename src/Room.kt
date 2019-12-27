package com.example

data class Room(
    val id: String,
    val price: String,
    val source: String,
    val address: String,
    var lat: Double?,
    var lon: Double?,
    val url: String
)