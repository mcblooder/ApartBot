package com.example.RoomProviders.Bpru

import com.example.Models.Room
import com.example.Protocols.FetchingStrategy
import io.ktor.client.HttpClient
import org.jsoup.Jsoup

class BpruFetchingStrategy: FetchingStrategy {

    override val uniquePrefix: String
        get() = "bpru"

    override fun fetchRooms(http: HttpClient): List<Room> {
        val document = Jsoup.connect("https://tomsk.bpru.ru/snyat").get()

        val ids = document.select("div[class=ov1] > div.des-item > div > a").map {
            it.attr("href").split("/").last()
        }

        val addresses = document.select("div[class=ov1] > div.des-item > p").map {
            it.text().removeRange(0..9)
        }

        val prices = document.select("div[class=ov1] > div.r-price1 > span").map {
            it.text()
        }

        val adsCount = listOf(ids.count(), addresses.count(), prices.count()).min() ?: 0

        return (0 until adsCount).map {
            Room(ids[it],
                prices[it],
                uniquePrefix,
                addresses[it],
                null,
                null,
                "https://tomsk.bpru.ru/realty/${ids[it]}"
            )
        }
    }
}