package com.example.fetchingStrategies.avito

import com.example.models.Room
import com.example.protocols.FetchingStrategy
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import kotlinx.coroutines.runBlocking

class RentAvitoFetchingStrategy: FetchingStrategy {

    override val uniquePrefix: String
        get() = "avito"

    override fun fetchRooms(http: HttpClient): List<Room> {
        val rooms = runBlocking {
            val avitoResponse = http.get<AvitoResponse>("https://m.avito.ru/api/9/items?key=af0deccbgcgidddjgnvljitntccdduijhdinfgjgfjir&districtId[]=370&owner[]=private&sort=date&locationId=657600&categoryId=24&params[201]=1060&params[504]=5256&page=1&display=list&limit=30") {
                accept(ContentType.Application.Json)
            }

            return@runBlocking avitoResponse.result.items.mapNotNull {
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
        }

       return rooms
    }
}