package com.example.RoomProviders.Neagent

import com.example.Room
import com.example.RoomProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.*
import kotlinx.coroutines.runBlocking


class NeagentRoomProvider: RoomProvider {

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

            val featureCollection = http.get<FeatureCollection>("https://neagent.info/map/load/?b=56.45498140215208%2C84.96683794263605%2C56.462115714391274%2C84.97833925489186&z=17&t=arenda&sid=45") {
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