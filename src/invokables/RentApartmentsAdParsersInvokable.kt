package com.example.invokables

import services.geo.Boundary
import services.geo.Point
import com.example.TelegramBot
import com.example.fetchingStrategies.avito.RentAvitoFetchingStrategy
import com.example.fetchingStrategies.bpru.BpruFetchingStrategy
import com.example.fetchingStrategies.neagent.NeagentFetchingStrategy
import com.example.fetchingStrategies.ru09.RentRu09FetchingStrategy
import com.example.models.Room
import com.example.protocols.FetchingStrategy
import com.example.protocols.IntervalInvokable
import com.example.services.geo.GeoService
import io.ktor.client.HttpClient
import java.io.File
import java.lang.Exception
import java.util.concurrent.locks.ReentrantLock


class RentApartmentsAdParsersInvokable(
    private val tg: TelegramBot,
    private val http: HttpClient,
    private val gc: GeoService
): IntervalInvokable {

    private val polygonOfInterest = Boundary(arrayOf(
        Point(56.455492, 84.973916),
        Point(56.457597, 84.971470),
        Point(56.457856, 84.972470),
        Point(56.458456, 84.971696),
        Point(56.459160, 84.973582),
        Point(56.458697, 84.975691),
        Point(56.456370, 84.975742)
    ))

    private val fetchingStrategies: List<FetchingStrategy> = listOf(
        RentAvitoFetchingStrategy(),
        BpruFetchingStrategy(),
        RentRu09FetchingStrategy(),
        NeagentFetchingStrategy()
    )

    /**
     * @param interval
     * Interval in seconds
     */
    override val interval: Int
        get() = 600

    override val description: String
        get() = "Rent Apartment Ad WebSites Parser"

    private val newIdentifiers = ArrayList<String>()

    @Synchronized
    override fun invoke() {
        val idsFile = File("rent_ids.txt")

        if (!idsFile.exists()) {
            idsFile.createNewFile()
        }

        val locker = ReentrantLock()

        val identifiers = idsFile.readLines()
        newIdentifiers.clear()

        fetchingStrategies
            .flatMap {
                try {
                    return@flatMap it.fetchRooms(http)
                } catch (e: Exception) {
                    tg.sendMessage("${it::class.java}:: ${e.message}", chatId = 114650278L)
                }
                return@flatMap ArrayList<Room>()
            }
            .parallelStream()
            .forEach {
                if (identifiers.contains(it.uniqueIdentity)) return@forEach

                locker.lock()
                newIdentifiers.add(it.uniqueIdentity)
                locker.unlock()

                val geoPoints = ArrayList<Double>()

                if (it.lat != null && it.lon != null) {
                    it.address = gc.decode(it.lat!!, it.lon!!).shortText
                    geoPoints.add(it.lat!!)
                    geoPoints.add(it.lon!!)
                } else {
                    gc.encode(it.address)?.point.let { points ->
                        geoPoints.addAll(points!!.reversed())
                    }
                }

                var isInteresting = false

                if (geoPoints.count() >= 2) {
                    isInteresting = polygonOfInterest.contains(Point(geoPoints[0], geoPoints[1]))
                }

                val text = "#${it.source}\n${it.address}, ${it.price} ₽\n${it.url}"
                val fire = "\uD83D\uDD25 #hot "

                tg.sendMessage(if (isInteresting) fire + text else text, chatId = -487315775L)
            }

        val textToAppend = newIdentifiers.map {
            "${it}\n"
        }.joinToString("")

        idsFile.appendText(textToAppend)
    }
}

