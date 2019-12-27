package com.example

import io.ktor.client.HttpClient

interface IntervalInvokable {
    val interval: Int
    fun invoke(tg: TelegramBot, http: HttpClient, gc: GeocodingService)
}