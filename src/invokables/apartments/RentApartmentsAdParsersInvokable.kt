package com.example.invokables.apartments

import services.geo.Boundary
import services.geo.Point
import com.example.TelegramBot
import com.example.fetchingStrategies.avito.RentAvitoFetchingStrategy
import com.example.fetchingStrategies.bpru.BpruFetchingStrategy
import com.example.fetchingStrategies.neagent.NeagentFetchingStrategy
import com.example.fetchingStrategies.ru09.RentRu09FetchingStrategy
import com.example.persistent.DB
import com.example.services.geo.GeoService
import io.ktor.client.HttpClient


class RentApartmentsAdParsersInvokable(tg: TelegramBot, http: HttpClient, gc: GeoService, db: DB) :
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

