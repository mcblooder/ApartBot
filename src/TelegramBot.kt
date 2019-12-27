package com.example

import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

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
}