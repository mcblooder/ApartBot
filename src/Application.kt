package com.example

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.UserAgent
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.css.*
import kotlinx.html.*
import org.apache.http.HttpHost
import org.jsoup.Jsoup
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.ApiContext
import org.telegram.telegrambots.meta.TelegramBotsApi
import java.net.Authenticator
import java.net.PasswordAuthentication


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    var cookieStorage = AcceptAllCookiesStorage()

    val client = HttpClient(Apache) {
        engine {
            customizeClient {
                setProxy(HttpHost("127.0.0.1", 8888))
            }
        }
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

    val geocodingService = GeocodingService(client)
    val tg = createTelegramBot()
    val adParsersInvokable = AdParsersInvokable()

    runBlocking {
        // Sample for making a HTTP Client request
        /*
        val message = client.post<JsonSampleClass> {
            url("http://127.0.0.1:8080/path/to/endpoint")
            contentType(ContentType.Application.Json)
            body = JsonSampleClass(hello = "world")
        }
        */
    }

    routing {
        get("/") {
//            val kek = client.get<String>("https://pastebin.com/raw/n54ynQY5")
            //val lal = client.get<EmployeeResponse>("https://api.myjson.com/bins/ofg7w")
//            call.respondText(lal.employees.joinToString {
//                it.name + " " + it.email
//            }, contentType = ContentType.Text.Plain)
//            val document = Jsoup.connect("https://neagent.info/tomsk/sdam-odno-komnatnuyu-kvartiru/kirovskiy/").get()
//            val response = document.select("tr[class^=infoblock4]").map {
//                it.select("span[itemprop=name]").text() + "\t\t\t\t" + it.select("span[itemprop=price]").text()
//            }
//            call.respondText(response.joinToString("\n"))
            cookieStorage = AcceptAllCookiesStorage()
            GlobalScope.launch {
                adParsersInvokable.invoke(tg, client, geocodingService)
            }

            call.respondText("Ok\n")
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
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
