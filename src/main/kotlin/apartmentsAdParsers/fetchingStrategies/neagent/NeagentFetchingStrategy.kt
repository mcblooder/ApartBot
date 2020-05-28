package com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.neagent

import com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.avito.AvitoResponse
import com.example.main.kotlin.apartmentsAdParsers.models.Room
import com.example.main.kotlin.apartmentsAdParsers.interfaces.FetchingStrategy
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import kotlin.collections.ArrayList


class NeagentFetchingStrategy: FetchingStrategy {

    override val uniquePrefix: String
        get() = "neagent"

    override fun fetchRooms(http: OkHttpClient): List<Room> {

        if (Date().minutes > 5) {
            return ArrayList<Room>()
        }

        val request1 = Request.Builder()
            .url("https://neagent.info/")
            .build();

        http.newCall(request1).execute();

        val request2 = Request.Builder()
            .url("https://neagent.info/map/tomsk/arenda/sdam-odno-komnatnuyu-kvartiru")
            .build();

        http.newCall(request1).execute();

        val roomsRequest = Request.Builder()
            .header("Accept", "application/json")
            .header("X-Requested-With", "XMLHttpRequest")
            .header("Referer", "https://neagent.info/map/tomsk/arenda/sdam-odno-komnatnuyu-kvartiru")
            .url("https://neagent.info/map/load/?b=56.454937294900915,84.96891904692384,56.45991542231753,84.97898269514774&z=17&t=arenda&sid=45")
            .build();


        val httpResponse = http.newCall(roomsRequest).execute()
        val neagentResponse = Gson().fromJson(httpResponse.body()?.charStream(), FeatureCollection::class.java)

        if (neagentResponse != null) {
            return neagentResponse.features.map {
                Room(
                    it.properties.id,
                    it.properties.price,
                    uniquePrefix,
                    it.properties.street,
                    it.geometry.coordinates.getOrNull(0)?.toDoubleOrNull(),
                    it.geometry.coordinates.getOrNull(1)?.toDoubleOrNull(),
                    "https://neagent.info/tomsk/${it.properties.id}/"
                )
            }
        } else {
            return ArrayList<Room>()
        }
    }
}