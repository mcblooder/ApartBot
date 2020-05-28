package com.example.main.kotlin

import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendLocation
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class TelegramBot(options: DefaultBotOptions?) : TelegramLongPollingBot(options) {

    override fun getBotUsername(): String {
        return "ApartBot"
    }

    override fun getBotToken(): String {
        return System.getenv("APART_BOT_TOKEN")
    }

    override fun onUpdateReceived(update: Update?) {
        println(update)
    }

    fun sendLocation(latitude: Float, longitude: Float, chatId: Long) {
//        println(latitude, longitude)
//        return;
        val sendLocation = SendLocation(latitude, longitude)
        sendLocation.chatId = chatId.toString()

        try {
            execute(sendLocation)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun sendMessage(text: String, chatId: Long, silent: Boolean = false) {
//        println(text)
//        return;
        val sendMessage = SendMessage(chatId, text)

        if (silent) {
            sendMessage.disableNotification()
        }

        try {
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }
}