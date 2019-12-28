package com.example.Invokables

import services.geo.Boundary
import services.geo.Point
import com.example.Protocols.IntervalInvokable
import com.example.Protocols.FetchingStrategy
import com.example.RoomProviders.Bpru.BpruFetchingStrategy
import com.example.RoomProviders.Ru09.Ru09FetchingStrategy
import com.example.Services.Geocoding.GeoService
import com.example.TelegramBot
import io.ktor.client.HttpClient
import java.io.File
import java.util.concurrent.locks.ReentrantLock


class AdParsersInvokable(
    private val tg: TelegramBot,
    private val http: HttpClient,
    private val gc: GeoService): IntervalInvokable {

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
//        NeagentRoomProvider(),
        Ru09FetchingStrategy(),
        BpruFetchingStrategy()
    )

    /**
     * @param interval
     * Interval in seconds
     */
    override val interval: Int
        get() = 600

    override val description: String
        get() = "Apartment Ad WebSites Parser"

    private val newIdentifiers = ArrayList<String>()

    @Synchronized
    override fun invoke() {
        val idsFile = File("ids.txt")

        if (!idsFile.exists()) {
            idsFile.createNewFile()
        }

        val locker = ReentrantLock()

        val identifiers = idsFile.readLines()
        newIdentifiers.clear()

        fetchingStrategies
            .flatMap { it.fetchRooms(http) }
            .parallelStream()
            .forEach {
                if (identifiers.contains(it.uniqueIdentity)) return@forEach

                locker.lock()
                newIdentifiers.add(it.uniqueIdentity)
                locker.unlock()

                val text = "#${it.source}\n${it.address}, ${it.price} ₽\n${it.url}"
                val geoPoints = gc.encode(it.address)?.point

                var isInteresting = false

                if (geoPoints != null && geoPoints.count() >= 2) {
                    isInteresting = polygonOfInterest.contains(Point(geoPoints[1], geoPoints[0]))
                }

                val fire = "\uD83D\uDD25 "

                tg.sendMessage(if (isInteresting) fire + text else text)
            }

        val textToAppend = newIdentifiers.map {
            "${it}\n"
        }.joinToString("")

        idsFile.appendText(textToAppend)
    }
}

