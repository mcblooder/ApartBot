package com.example.main.kotlin

import com.example.main.kotlin.apartmentsAdParsers.RentApartmentsAdParsersInvokable
import com.example.main.kotlin.decathlon.DecathlonDeliveryParserInvokable
import com.example.main.kotlin.generalAvitoParsers.GeneralAvitoAdParsersInvokable
import com.example.main.kotlin.interfaces.IntervalInvokable
import com.example.main.kotlin.persistent.DB
import com.example.main.kotlin.services.geo.GeoService
import com.sun.net.httpserver.HttpServer
import okhttp3.OkHttpClient
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.ApiContext
import org.telegram.telegrambots.meta.TelegramBotsApi
import java.io.PrintWriter
import java.net.Authenticator
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*
import kotlin.collections.HashMap
import kotlin.concurrent.fixedRateTimer


fun main(args: Array<String>) {

    val client = createHttpClient()

    val geoService = GeoService(client)
    val db = DB()
    val tg = createTelegramBot()

    val invokables = listOf(
        RentApartmentsAdParsersInvokable(tg, client, geoService, db),
        GeneralAvitoAdParsersInvokable(tg, client, geoService, db)
    )

    printMemoryUsageEveryMinute()

    val runTimes = HashMap<IntervalInvokable, Date>()

    invokables.forEach {
        runTimes[it] = Date()
        it.invoke()
    }

    /*
        invokables.forEach {
            fixedRateTimer(
                name = UUID.randomUUID().toString(),
                initialDelay = 0,
                period = it.interval * 1000L) {
                runTimes[it] = Date()
                cookieStorage = AcceptAllCookiesStorage()
                it.invoke()
            }
        }
        */

    HttpServer.create(InetSocketAddress(8080), 0).apply {

        createContext("/") { http ->
            http.responseHeaders.add("Content-type", "text/plain")
            http.sendResponseHeaders(200, 0)

            val response = runTimes.map {
                val date = SimpleDateFormat("dd.MM.yyyy HH:mm").format(it.value)
                val nextRunMs = (it.value.time + it.key.interval * 1000) - Date().time
                val nextRun = TimeUnit.MILLISECONDS.toSeconds(nextRunMs).toString()
                return@map "${it.key.description}: $date | $nextRun"
            }

            PrintWriter(http.responseBody).use { out ->
                out.println(response)
            }
        }

        start()
    }
}

fun printMemoryUsageEveryMinute() {
    fixedRateTimer(
            name = "memoryPrint",
            initialDelay = 0,
            period = 60000) {
        val memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        println("${Date().time}| Memory used: ${memory / 1024.0 / 1024.0} mb")
    }
}

fun createHttpClient(): OkHttpClient {
    val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
        object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    )

    // Install the all-trusting trust manager

    // Install the all-trusting trust manager
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())
    // Create an ssl socket factory with our all-trusting manager
    // Create an ssl socket factory with our all-trusting manager
    val sslSocketFactory = sslContext.getSocketFactory()

    val builder = OkHttpClient.Builder()
    builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
    builder.hostnameVerifier(object : HostnameVerifier {
        override fun verify(hostname: String?, session: SSLSession?): Boolean {
            return true
        }
    })

    builder.addInterceptor { chain ->
        val newRequest = chain.request().newBuilder()
            .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3945.88 Safari/537.36")
            .build()
        return@addInterceptor chain.proceed(newRequest)
    }

    return builder.build()
}

fun createTelegramBot(): TelegramBot {

    ApiContextInitializer.init()

    val botsApi = TelegramBotsApi()

    val botOptions: DefaultBotOptions = ApiContext.getInstance(DefaultBotOptions::class.java)

//    botOptions.proxyHost = ""
//    botOptions.proxyPort = 1080
//    botOptions.proxyType = DefaultBotOptions.ProxyType.SOCKS5

    val bot = TelegramBot(botOptions)
    botsApi.registerBot(bot)

    return bot
}