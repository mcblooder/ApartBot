package com.example.main.kotlin.services.geo;

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import kotlin.random.Random

class GeoService(val http: OkHttpClient) {

    fun decode(lat: Double, lon: Double): GeocoderResponse {
        val geoRequest = Request.Builder()
            .url("https://taxi.yandex.ru/3.0/nearestposition/")
            .header("accept", "application/json")
            .header("x-requested-with", "XMLHttpRequest")
            .header("x-requested-uri", "https://taxi.yandex.ru/")
            .header("referer", "https://taxi.yandex.ru/")
            .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"ll\":[${lon},${lat}],\"dx\":0,\"not_sticky\":true}"))
            .build();

        val httpResponse = http.newCall(geoRequest).execute()

        return Gson().fromJson(httpResponse.body()?.charStream(), GeocoderResponse::class.java)
    }

    fun encode(address: String): GeoObject? {

        val c1 = 85.057656 - Random.nextDouble() / 10000.0
        val c2 = 56.492985 + Random.nextDouble() / 10000.0

        val body = "{\"ll\":[${c1},${c2}],\"results\":6,\"skip\":0,\"sort\":\"dist\",\"what\":\"${address}\"}"

        val geoRequest = Request.Builder()
            .url("https://taxi.yandex.ru/geosearch/")
            .header("accept", "application/json")
            .header("x-requested-with", "XMLHttpRequest")
            .header("x-requested-uri", "https://taxi.yandex.ru/")
            .header("referer", "https://taxi.yandex.ru/")
            .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body))
            .build();

        val httpResponse = http.newCall(geoRequest).execute()

        return Gson().fromJson(httpResponse.body()?.charStream(), GeoEncoderResponse::class.java).objects.firstOrNull()
    }
}

