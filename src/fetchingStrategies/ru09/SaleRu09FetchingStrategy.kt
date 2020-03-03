package com.example.fetchingStrategies.ru09

import com.example.exceptions.AdParsingException
import com.example.models.Room
import com.example.protocols.FetchingStrategy
import io.ktor.client.HttpClient
import org.jsoup.Jsoup

class SaleRu09FetchingStrategy: FetchingStrategy {

    override val uniquePrefix: String
        get() = "ru09"

    override fun fetchRooms(http: HttpClient): List<Room> {
        val document = Jsoup.connect("https://www.tomsk.ru09.ru/realty/?perpage=50&type=1&otype=1&price[max]=4500&knum[2]=on&knum[3]=on&knum[4]=on&is_studio[value]=on&district[1]=on&scom[min]=35&mat[1]=on&mat[2]=on&mat[3]=on&mat[4]=on&mat[6]=on&mat[7]=on&mat[8]=on").get()

        val ids = document.select("a[class=visited_ads]").map {
            it.attr("href").split("=").last()
        }

        val addresses = document.select("a[class=map_link]").map {
            it.text()
        }

        val prices = document.select("table[class=realty] > tbody > tr > td.last > p > span > strong").map {
            val price = it.text().toDoubleOrNull()
            if (price != null) {
                return@map (price * 1000.0).toInt()
            }
            return@map ""
        }

        if (ids.count() != addresses.count()) {
            throw AdParsingException("Number of parsed pieces do not match")
        }

        if (ids.count() != prices.count()) {
            return (0 until ids.count()).map {
                Room(
                    ids[it],
                    "?",
                    uniquePrefix,
                    addresses[it],
                    null,
                    null,
                    "https://www.tomsk.ru09.ru/realty?subaction=detail&id=${ids[it]}"
                )
            }
        } else {
            return (0 until ids.count()).map {
                Room(
                    ids[it],
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
}