package com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.ru09

import com.example.main.kotlin.apartmentsAdParsers.exceptions.AdParsingException
import com.example.main.kotlin.apartmentsAdParsers.models.Room
import com.example.main.kotlin.apartmentsAdParsers.interfaces.FetchingStrategy
import okhttp3.OkHttpClient
import org.jsoup.Jsoup

class RentRu09FetchingStrategy: FetchingStrategy {

    override val uniquePrefix: String
        get() = "ru09"

    override fun fetchRooms(http: OkHttpClient): List<Room> {
        val document = Jsoup.connect("https://www.tomsk.ru09.ru/realty/?perpage=50&type=2&otype=1&rent_type[1]=on").get()

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