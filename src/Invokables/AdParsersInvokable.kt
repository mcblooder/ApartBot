package com.example.Invokables

import Services.Geo.Boundary
import Services.Geo.Point
import com.example.Protocols.IntervalInvokable
import com.example.Protocols.RoomProvider
import com.example.RoomProviders.Bpru.BpruRoomProvider
import com.example.RoomProviders.Ru09.Ru09RoomProvider
import com.example.Services.Geocoding.GeoService
import com.example.TelegramBot
import io.ktor.client.HttpClient
import java.io.File
import java.util.concurrent.locks.ReentrantLock


class AdParsersInvokable: IntervalInvokable {

    private val polygonOfInterest = Boundary(arrayOf(
        Point(56.455492, 84.973916),
        Point(56.457597, 84.971470),
        Point(56.457856, 84.972470),
        Point(56.458456, 84.971696),
        Point(56.459160, 84.973582),
        Point(56.458697, 84.975691),
        Point(56.456370, 84.975742)
    ))

    private val roomProviders: List<RoomProvider> = listOf(
//        NeagentRoomProvider(),
        Ru09RoomProvider(),
        BpruRoomProvider()
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
    override fun invoke(tg: TelegramBot, http: HttpClient, gc: GeoService) {
        val idsFile = File("ids.txt")

        if (!idsFile.exists()) {
            idsFile.createNewFile()
        }

        val locker = ReentrantLock()

        val identifiers = idsFile.readLines()
        newIdentifiers.clear()

        roomProviders
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

