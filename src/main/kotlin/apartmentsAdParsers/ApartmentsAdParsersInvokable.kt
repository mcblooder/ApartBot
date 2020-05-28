package com.example.main.kotlin.apartmentsAdParsers

import com.example.main.kotlin.persistent.storages.UniqueKeyStorage
import com.example.main.kotlin.TelegramBot
import com.example.main.kotlin.apartmentsAdParsers.models.Room
import com.example.main.kotlin.apartmentsAdParsers.interfaces.FetchingStrategy
import com.example.main.kotlin.interfaces.IntervalInvokable
import com.example.main.kotlin.persistent.DB
import com.example.main.kotlin.services.geo.GeoService
import okhttp3.OkHttpClient
import services.geo.Boundary
import services.geo.Point
import java.lang.Exception
import java.util.concurrent.locks.ReentrantLock

abstract class ApartmentsAdParsersInvokable(
    private val tg: TelegramBot,
    private val http: OkHttpClient,
    private val gc: GeoService,
    private val db: DB
): IntervalInvokable {

    override var description: String = "unknown"

    open val groupChatId: Long = 0L
    open val polygonOfInterest = Boundary(arrayOf())
    open val fetchingStrategies: List<FetchingStrategy> = listOf()

    private val apartmentsStorage = UniqueKeyStorage(db)

    /**
     * @param interval
     * Interval in seconds
     */
    override val interval: Int
        get() = 600

    @Synchronized
    override fun invoke() {
        val locker = ReentrantLock()

        fetchingStrategies
            .flatMap {
                try {
                    return@flatMap it.fetchRooms(http)
                } catch (e: Exception) {
                    tg.sendMessage("${it::class.java}:: ${e.message}", chatId = 114650278L, silent = true)
                }
                return@flatMap ArrayList<Room>()
            }
            .parallelStream()
            .forEach {
                if (apartmentsStorage.checkKeyExist(it.uniqueIdentity)) return@forEach

                locker.lock()
                apartmentsStorage.addUniqueKey(it.uniqueIdentity)
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

                tg.sendMessage(if (isInteresting) fire + text else text, chatId = groupChatId)
            }
    }
}