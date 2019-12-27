package com.example

import com.example.RoomProviders.Neagent.NeagentRoomProvider
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import io.ktor.client.HttpClient


class AdParsersInvokable: IntervalInvokable {

    private val roomProviders: List<RoomProvider> = listOf(
        NeagentRoomProvider()
    )

    override val interval: Int
        get() = 300

    @Synchronized
    override fun invoke(tg: TelegramBot, http: HttpClient, gc: GeocodingService) {
        roomProviders
            .flatMap { it.fetchRooms(http) }
            .parallelStream()
            .forEach {
                val text = "#${it.source} ${it.address} ${it.price}\n${it.url}"
                if (it.lat != null && it.lon != null) {
                    println(gc.decode(it.lat!!, it.lon!!))
                }
//                val sendMessage = SendMessage(RODION_CHAT_ID, text)
//
//                try {
//                    tg.execute(sendMessage)
//                } catch (e: TelegramApiException) {
//                    e.printStackTrace()
//                }
            }
    }

    companion object {
        const val RODION_CHAT_ID = 114650278L
    }
}