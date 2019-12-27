package com.example

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import kotlinx.coroutines.runBlocking

class GeocodingService(val http: HttpClient) {

    fun decode(lat: Double, lon: Double): String {
        return runBlocking {
            val json = http.post<JsonObject>("https://taxi.yandex.ru/3.0/nearestposition/") {
                accept(ContentType.Application.Json)
                header("x-requested-with", "XMLHttpRequest")
                header("x-requested-uri", "https://taxi.yandex.ru/")
                header("referer", "https://taxi.yandex.ru/")
                body = TextContent("{\"ll\":[${lon},${lat}],\"dx\":0,\"not_sticky\":true}", contentType = ContentType.Application.Json)
            }

            return@runBlocking json["short_text"].asString
        }
    }



}

