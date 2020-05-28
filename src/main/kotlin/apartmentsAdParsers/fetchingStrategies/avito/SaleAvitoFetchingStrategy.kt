package com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.avito

import com.example.main.kotlin.apartmentsAdParsers.models.Room
import com.example.main.kotlin.apartmentsAdParsers.interfaces.FetchingStrategy
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request

class SaleAvitoFetchingStrategy: FetchingStrategy {

    override val uniquePrefix: String
        get() = "avito"

    override fun fetchRooms(http: OkHttpClient): List<Room> {
        val roomsRequest = Request.Builder()
            .url("https://m.avito.ru/api/9/items?key=af0deccbgcgidddjgnvljitntccdduijhdinfgjgfjir&categoryId=24&params[201]=1059&locationId=657600&districtId[]=370&priceMax=4500000&params[549][]=5697&params[549][]=5698&params[549][]=414718&params[549][]=5695&params[549][]=5699&params[578-from-int]=35&params[498][]=5244&params[498][]=5247&params[498][]=5245&params[498][]=5246&sort=date&withImagesOnly=false&page=1&display=list&limit=30")
            .build();

        val httpResponse = http.newCall(roomsRequest).execute();
        val avitoResponse = Gson().fromJson(httpResponse.body()?.charStream(), AvitoResponse::class.java)

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