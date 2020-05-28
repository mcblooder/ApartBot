package com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.cian

import com.example.main.kotlin.apartmentsAdParsers.exceptions.AdParsingException
import com.example.main.kotlin.apartmentsAdParsers.models.Room
import com.example.main.kotlin.apartmentsAdParsers.interfaces.FetchingStrategy
import okhttp3.OkHttpClient
import org.jsoup.Jsoup

class SaleCianFetchingStrategy: FetchingStrategy {

    override val uniquePrefix: String
        get() = "cian"

    override fun fetchRooms(http: OkHttpClient): List<Room> {
        val document = Jsoup.connect("https://tomsk.cian.ru/cat.php?currency=2&deal_type=sale&district%5B0%5D=3580&district%5B10%5D=3612&district%5B11%5D=3614&district%5B12%5D=3615&district%5B13%5D=3617&district%5B14%5D=3618&district%5B15%5D=3619&district%5B16%5D=3620&district%5B17%5D=3622&district%5B1%5D=3592&district%5B2%5D=3593&district%5B3%5D=3595&district%5B4%5D=3596&district%5B5%5D=3604&district%5B6%5D=3605&district%5B7%5D=3606&district%5B8%5D=3610&district%5B9%5D=3611&engine_version=2&maxprice=4500000&offer_type=flat&room2=1&room3=1&room4=1&room5=1&room6=1&room7=1&room9=1&totime=3600").get()

        val ads = document.select("#frontend-serp > div > div:nth-child(5)")

        val ids = ads.select("a[class*=--header]").map {
            it.attr("href").split("/").dropLast(1).last()
        }

        val addresses = ads.select("div[class*=--address-links]").map {
            it.text()
        }

        val prices = ads.select("div[class*=--header]").map {
            return@map it.text().replace(" ₽", "")
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
                    "https://tomsk.cian.ru/sale/flat/${ids[it]}"
                )
            }
        } else {
            return (0 until ids.count()).map {
                Room(
                    ids[it],
                    prices[it],
                    uniquePrefix,
                    addresses[it],
                    null,
                    null,
                    "https://tomsk.cian.ru/sale/flat/${ids[it]}"
                )
            }
        }
    }
}