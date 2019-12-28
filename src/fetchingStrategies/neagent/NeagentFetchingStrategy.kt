package com.example.RoomProviders.Neagent

import com.example.Models.Room
import com.example.Protocols.FetchingStrategy
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.*
import kotlinx.coroutines.runBlocking


class NeagentFetchingStrategy: FetchingStrategy {

    override val uniquePrefix: String
        get() = "neagent"

    override fun fetchRooms(http: HttpClient): List<Room> {
        val rooms = runBlocking {
            http.call("https://neagent.info/") {
                method = HttpMethod.Get
            }

            http.call("https://neagent.info/map/tomsk/arenda/sdam-odno-komnatnuyu-kvartiru") {
                method = HttpMethod.Get
            }

            val featureCollection = http.get<FeatureCollection>("https://neagent.info/map/load/?b=56.454937294900915,84.96891904692384,56.45991542231753,84.97898269514774&z=17&t=arenda&sid=45") {
                accept(ContentType.Application.Json)
                header("X-Requested-With", "XMLHttpRequest")
                header("Referer", "https://neagent.info/map/tomsk/arenda/sdam-odno-komnatnuyu-kvartiru")
            }

            return@runBlocking featureCollection.features.map {
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
        }

        return rooms
    }
}