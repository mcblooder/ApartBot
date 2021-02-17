package com.example.main.kotlin.generalAvitoParsers

import com.example.main.kotlin.TelegramBot
import com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.avito.SaleAvitoFetchingStrategy
import com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.cian.SaleCianFetchingStrategy
import com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.ru09.SaleRu09FetchingStrategy
import com.example.main.kotlin.apartmentsAdParsers.models.Room
import com.example.main.kotlin.generalAvitoParsers.fetchingStrategies.GPUAvitoFetchingStrategy
import com.example.main.kotlin.generalAvitoParsers.models.AvitoAd
import com.example.main.kotlin.interfaces.IntervalInvokable
import com.example.main.kotlin.persistent.DB
import com.example.main.kotlin.persistent.storages.UniqueKeyStorage
import com.example.main.kotlin.services.geo.GeoService
import okhttp3.OkHttpClient
import services.geo.Point
import java.lang.Exception
import java.util.concurrent.locks.ReentrantLock

class GeneralAvitoAdParsersInvokable(
    private val tg: TelegramBot,
    private val http: OkHttpClient,
    private val gc: GeoService,
    private val db: DB
): IntervalInvokable {

    override var description: String = "general Avito ads parser"

    /**
     * @param interval
     * Interval in seconds
     */
    override val interval: Int
        get() = 600


    private val fetchingStrategies = listOf(
        GPUAvitoFetchingStrategy()
    )

    private val adsStorage = UniqueKeyStorage(db)
    private val groupChatId = -595815217L

    @Synchronized
    override fun invoke() {
        val locker = ReentrantLock()

        fetchingStrategies
            .flatMap {
                try {
                    return@flatMap it.fetchAds(http)
                } catch (e: Exception) {
                    tg.sendMessage("${it::class.java}:: ${e.message}", chatId = 114650278L, silent = true)
                }
                return@flatMap ArrayList<AvitoAd>()
            }
            .parallelStream()
            .forEach {
                if (adsStorage.checkKeyExist(it.uniqueIdentity)) return@forEach

                locker.lock()
                adsStorage.addUniqueKey(it.uniqueIdentity)
                locker.unlock()


                val text = "${it.title}\n${it.price} ₽\n${it.url}"

                tg.sendMessage(text, chatId = groupChatId)
            }
    }
}