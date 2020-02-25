package com.example.fetchingStrategies.avito

import com.example.models.Room
import com.example.protocols.FetchingStrategy
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import kotlinx.coroutines.runBlocking

class SaleAvitoFetchingStrategy: FetchingStrategy {

    override val uniquePrefix: String
        get() = "avito"

    override fun fetchRooms(http: HttpClient): List<Room> {
        val rooms = runBlocking {
            val avitoResponse = http.get<AvitoResponse>("https://m.avito.ru/api/9/items?key=af0deccbgcgidddjgnvljitntccdduijhdinfgjgfjir&categoryId=24&params[201]=1059&locationId=657600&districtId[]=370&params[549][]=5696&params[549][]=5697&params[549][]=5698&params[549][]=414718&params[549][]=5695&sort=date&withImagesOnly=false&page=1&display=list&limit=30") {
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