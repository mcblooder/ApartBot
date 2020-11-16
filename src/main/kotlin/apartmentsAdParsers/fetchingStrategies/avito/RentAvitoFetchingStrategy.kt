package com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.avito

import com.example.main.kotlin.apartmentsAdParsers.models.Room
import com.example.main.kotlin.apartmentsAdParsers.interfaces.FetchingStrategy
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request

class RentAvitoFetchingStrategy: FetchingStrategy {

    override val uniquePrefix: String
        get() = "avito"

    override fun fetchRooms(http: OkHttpClient): List<Room> {
        val roomsRequest = Request.Builder()
            .url("https://m.avito.ru/api/9/items?key=af0deccbgcgidddjgnvljitntccdduijhdinfgjgfjir&categoryId=24&params[201]=1060&locationId=657600&params[504]=5256&priceMin=11000&params[550][]=5702&params[550][]=5703&params[550][]=5704&params[550][]=5705&params[110486]=false&params[1460]=false&owner[]=private&sort=date&withImagesOnly=false&forceLocation=true")
            .build();

        val httpResponse = http.newCall(roomsRequest).execute();
        val avitoResponse = Gson().fromJson(httpResponse.body?.charStream(), AvitoResponse::class.java)

        if (avitoResponse != null) {
            return avitoResponse.result.items.mapNotNull {
                if (it.value.price == null || it.value.address == null) {
                    return@mapNotNull null
                }
                Room(
                    it.value.id.toString(),
                    it.value.price,
                    uniquePrefix,
                    it.value.address ?: "",
                    it.value.coords?.lat?.toDoubleOrNull(),
                    it.value.coords?.lng?.toDoubleOrNull(),
                    "https://avito.ru${it.value.uriMWeb}/"
                )
            }
        } else {
            return ArrayList<Room>()
        }
    }
}