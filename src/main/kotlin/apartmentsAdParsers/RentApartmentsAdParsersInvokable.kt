package com.example.main.kotlin.apartmentsAdParsers

import services.geo.Boundary
import services.geo.Point
import com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.avito.RentAvitoFetchingStrategy
import com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.bpru.BpruFetchingStrategy
import com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.neagent.NeagentFetchingStrategy
import com.example.main.kotlin.apartmentsAdParsers.fetchingStrategies.ru09.RentRu09FetchingStrategy
import com.example.main.kotlin.TelegramBot
import com.example.main.kotlin.persistent.DB
import com.example.main.kotlin.services.geo.GeoService
import okhttp3.OkHttpClient


class RentApartmentsAdParsersInvokable(tg: TelegramBot, http: OkHttpClient, gc: GeoService, db: DB) :
    ApartmentsAdParsersInvokable(tg, http, gc, db) {

    override var description = "Rent Apartment Ad WebSites Parser"
    override val groupChatId = -487315775L

    override val polygonOfInterest = Boundary(arrayOf(
            Point(56.455492, 84.973916),
            Point(56.457597, 84.971470),
            Point(56.457856, 84.972470),
            Point(56.458456, 84.971696),
            Point(56.459160, 84.973582),
            Point(56.458697, 84.975691),
            Point(56.456370, 84.975742)
    ))

    override val fetchingStrategies = listOf(
            RentAvitoFetchingStrategy(),
            BpruFetchingStrategy(),
            RentRu09FetchingStrategy(),
            NeagentFetchingStrategy()
    )
}

