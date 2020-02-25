package com.example.services.geo;

import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class GeoService(val http: HttpClient) {

    fun decode(lat: Double, lon: Double): GeocoderResponse {
        return runBlocking {
            val response = http.post<GeocoderResponse>("https://taxi.yandex.ru/3.0/nearestposition/") {
                accept(ContentType.Application.Json)
                header("x-requested-with", "XMLHttpRequest")
                header("x-requested-uri", "https://taxi.yandex.ru/")
                header("referer", "https://taxi.yandex.ru/")
                body = TextContent("{\"ll\":[${lon},${lat}],\"dx\":0,\"not_sticky\":true}", contentType = ContentType.Application.Json)
            }

            return@runBlocking response
        }
    }

    fun encode(address: String): GeoObject? {
        return runBlocking {
            val response = http.post<GeoEncoderResponse>("https://taxi.yandex.ru/geosearch/") {
                accept(ContentType.Application.Json)
                header("x-requested-with", "XMLHttpRequest")
                header("x-requested-uri", "https://taxi.yandex.ru/")
                header("referer", "https://taxi.yandex.ru/")

                val c1 = 85.057656 - Random.nextDouble() / 10000.0
                val c2 = 56.492985 + Random.nextDouble() / 10000.0
                body = TextContent(
                    "{\"ll\":[${c1},${c2}],\"results\":6,\"skip\":0,\"sort\":\"dist\",\"what\":\"${address}\"}",
                    contentType = ContentType.Application.Json
                )
            }

            return@runBlocking response.objects.firstOrNull()
        }
    }
}

