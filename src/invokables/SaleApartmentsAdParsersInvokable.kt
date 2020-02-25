package com.example.invokables

import com.example.TelegramBot
import com.example.models.Room
import com.example.fetchingStrategies.avito.SaleAvitoFetchingStrategy
import com.example.fetchingStrategies.ru09.SaleRu09FetchingStrategy
import com.example.protocols.FetchingStrategy
import com.example.protocols.IntervalInvokable
import com.example.services.geo.GeoService
import io.ktor.client.HttpClient
import services.geo.Boundary
import services.geo.Point
import java.io.File
import java.lang.Exception
import java.util.concurrent.locks.ReentrantLock

class SaleApartmentsAdParsersInvokable(
    private val tg: TelegramBot,
    private val http: HttpClient,
    private val gc: GeoService
): IntervalInvokable {

    private val polygonOfInterest = Boundary(arrayOf(
        Point(56.4668584, 84.98375950000002),
        Point(56.4640134, 84.98933849999997),
        Point(56.4621641, 84.99251429999998),
        Point(56.4603147, 84.98951019999998),
        Point(56.4593662, 84.98745020000001),
        Point(56.4568526, 84.98238620000001),
        Point(56.4549081, 84.9756056),
        Point(56.453435, 84.97729809999998),
        Point(56.4517274, 84.97802760000002),
        Point(56.4513242, 84.97734100000002),
        Point(56.4516325, 84.97540980000002),
        Point(56.4531029, 84.97540980000002),
        Point(56.4537433, 84.97433690000003),
        Point(56.4540516, 84.9721912),
        Point(56.4550239, 84.97116119999998),
        Point(56.4552786, 84.96866820000002),
        Point(56.4553023, 84.95120170000001),
        Point(56.4715693, 84.95032579999997),
        Point(56.4716175, 84.9653758),
        Point(56.4690336, 84.96541869999999),
        Point(56.4689151, 84.9813833),
        Point(56.4669474, 84.98374360000003)
    ))

    private val fetchingStrategies: List<FetchingStrategy> = listOf(
        SaleAvitoFetchingStrategy(),
        SaleRu09FetchingStrategy()
    )

    /**
     * @param interval
     * Interval in seconds
     */
    override val interval: Int
        get() = 600

    override val description: String
        get() = "Sale Apartment Ad WebSites Parser"

    private val newIdentifiers = ArrayList<String>()

    @Synchronized
    override fun invoke() {
        val idsFile = File("sale_ids.txt")

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
                        geoPoints.addAll(points!!)
                    }
                }

                var isInteresting = false

                if (geoPoints.count() >= 2) {
                    isInteresting = polygonOfInterest.contains(Point(geoPoints[1], geoPoints[0]))
                }

                val text = "#${it.source}\n${it.address}, ${it.price} ₽\n${it.url}"
                val fire = "\uD83D\uDD25 #hot "

                tg.sendMessage(if (isInteresting) fire + text else text, chatId = -270950399L)
            }

        val textToAppend = newIdentifiers.map {
            "${it}\n"
        }.joinToString("")

        idsFile.appendText(textToAppend)
    }
}