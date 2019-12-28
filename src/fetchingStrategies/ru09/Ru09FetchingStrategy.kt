package com.example.RoomProviders.Ru09

import com.example.Models.Room
import com.example.Protocols.FetchingStrategy
import io.ktor.client.HttpClient
import org.jsoup.Jsoup

class Ru09FetchingStrategy: FetchingStrategy {

    override val uniquePrefix: String
        get() = "ru09"

    override fun fetchRooms(http: HttpClient): List<Room> {
        val document = Jsoup.connect("https://www.tomsk.ru09.ru/realty/?type=2&otype=1&district[1]=on&rent_type[1]=on&perpage=50").get()

        val ids = document.select("a[class=visited_ads]").map {
            it.attr("href").split("=").last()
        }

        val addresses = document.select("a[class=map_link]").map {
            it.text()
        }

        val prices = document.select("table[class=realty] > tbody > tr > td.last > p:nth-child(9) > span > strong").map {
            val price = it.text().toDoubleOrNull()
            if (price != null) {
                return@map (price * 1000.0).toInt()
            }
            return@map ""
        }

        val adsCount = listOf(ids.count(), addresses.count(), prices.count()).min() ?: 0

        return (0 until adsCount).map {
            Room(ids[it],
                "${prices[it]}",
                uniquePrefix,
                addresses[it],
                null,
                null,
                "https://www.tomsk.ru09.ru/realty?subaction=detail&id=${ids[it]}"
            )
        }
    }
}