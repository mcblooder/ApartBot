package com.example.main.kotlin.decathlon

import com.example.main.kotlin.persistent.storages.UniqueKeyStorage
import com.example.main.kotlin.decathlon.models.DecathlonResponse
import com.example.main.kotlin.TelegramBot
import com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.avito.AvitoResponse
import com.example.main.kotlin.persistent.DB
import com.example.main.kotlin.interfaces.IntervalInvokable
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.text.get

class DecathlonDeliveryParserInvokable(
    private val tg: TelegramBot,
    private val http: OkHttpClient,
    private val db: DB
): IntervalInvokable {

    override val interval: Int
        get() = 600

    override val description: String
        get() = "Decathlon Delivery Parser"

    private val trackNumber = "RUS3533707"
    private val orderStatusHisoryStorage = UniqueKeyStorage(db)

    override fun invoke() {
        val roomsRequest = Request.Builder()
            .header("Accept", "application/json")
            .url("https://api.decathlon.ru/order-tracking/order/RUS3533707")
            .build();

        val httpResponse = http.newCall(roomsRequest).execute();
        val decathlonResponse = Gson().fromJson(httpResponse.body?.charStream(), DecathlonResponse::class.java)

        decathlonResponse?.orderStatusHistory?.forEach {
            if (!orderStatusHisoryStorage.checkKeyExist(it.uniqueIdentity)) {
                orderStatusHisoryStorage.addUniqueKey(it.uniqueIdentity)
                val message = "${it.status.status} ${it.createdAt}\nShipment: ${decathlonResponse.estimatedShipmentDate}\nDelivery: ${decathlonResponse.estimatedDeliveryDate} ${decathlonResponse.trackNum ?: ""}"
                tg.sendMessage(message, chatId = 114650278L, silent = false)
            }
        }
    }
}