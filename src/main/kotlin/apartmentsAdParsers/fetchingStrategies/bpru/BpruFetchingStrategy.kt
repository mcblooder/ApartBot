package com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.bpru

import com.example.main.kotlin.apartmentsAdParsers.exceptions.AdParsingException
import com.example.main.kotlin.apartmentsAdParsers.models.Room
import com.example.main.kotlin.apartmentsAdParsers.interfaces.FetchingStrategy
import okhttp3.OkHttpClient
import org.jsoup.Jsoup

class BpruFetchingStrategy: FetchingStrategy {

    override val uniquePrefix: String
        get() = "bpru"

    override fun fetchRooms(http: OkHttpClient): List<Room> {
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

        if (ids.count() != addresses.count() || addresses.count() != prices.count()) {
            throw AdParsingException("Number of parsed pieces do not match")
        }

        return (0 until ids.count()).map {
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