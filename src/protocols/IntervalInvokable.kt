package com.example.Protocols

import com.example.Services.Geocoding.GeoService
import com.example.TelegramBot
import io.ktor.client.HttpClient

interface IntervalInvokable {
    val interval: Int
    val description: String
    fun invoke()
}