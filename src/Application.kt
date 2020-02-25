package com.example

import com.example.invokables.RentApartmentsAdParsersInvokable
import com.example.invokables.SaleApartmentsAdParsersInvokable
import com.example.protocols.IntervalInvokable
import com.example.services.geo.GeoService
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.UserAgent
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.CookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.css.*
import kotlinx.html.*
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.ApiContext
import org.telegram.telegrambots.meta.TelegramBotsApi
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.concurrent.fixedRateTimer

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    var cookieStorage = AcceptAllCookiesStorage()
    val client = createHttpClient(cookieStorage)

    val geoService = GeoService(client)
    val tg = createTelegramBot()

    val invokables = listOf(
        RentApartmentsAdParsersInvokable(tg, client, geoService),
        SaleApartmentsAdParsersInvokable(tg, client, geoService)
    )

    printMemoryUsageEveryMinute()

    val runTimes = HashMap<IntervalInvokable, Date>()

    cookieStorage = AcceptAllCookiesStorage()

    invokables.forEach {
        runTimes[it] = Date()
        it.invoke()
    }
//    invokables.forEach {
//        fixedRateTimer(
//            name = UUID.randomUUID().toString(),
//            initialDelay = 0,
//            period = it.interval * 1000L) {
//            runTimes[it] = Date()
//            cookieStorage = AcceptAllCookiesStorage()
//            it.invoke()
//        }
//    }

    routing {
        get("/") {
            val response = runTimes.map {
                val date = SimpleDateFormat("dd.MM.yyyy HH:mm").format(it.value)
                val nextRunMs = (it.value.time + it.key.interval * 1000) - Date().time
                val nextRun = TimeUnit.MILLISECONDS.toSeconds(nextRunMs).toString()
                return@map "${it.key.description}: $date | $nextRun"
            }
            call.respondText(response.toString())
        }
    }
}

fun printMemoryUsageEveryMinute() {
    fixedRateTimer(
        name = "memoryPrint",
        initialDelay = 0 ,
        period = 60000) {
        val memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        println("${Date().time}| Memory used: ${memory / 1024.0 / 1024.0 } mb")
    }
}

fun createHttpClient(cookieStorage: CookiesStorage): HttpClient {
    return HttpClient(Apache) {
        install(JsonFeature) {
            serializer = GsonSerializer()
            acceptContentTypes = acceptContentTypes + listOf(ContentType.Text.Html)
        }
        install(HttpCookies) {
            storage = cookieStorage
        }
        install(UserAgent) {
            agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36"
        }
    }
}

fun createTelegramBot(): TelegramBot {
    Authenticator.setDefault(object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication("mag", "EeChe9ow8Vei".toCharArray())
        }
    })

    ApiContextInitializer.init()

    val botsApi = TelegramBotsApi()

    val botOptions: DefaultBotOptions = ApiContext.getInstance(DefaultBotOptions::class.java)

    botOptions.proxyHost = "p.dev.magdv.com"
    botOptions.proxyPort = 1080
    botOptions.proxyType = DefaultBotOptions.ProxyType.SOCKS5

    val bot = TelegramBot(botOptions)
    botsApi.registerBot(bot)

    return bot
}

data class JsonSampleClass(val hello: String)

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
