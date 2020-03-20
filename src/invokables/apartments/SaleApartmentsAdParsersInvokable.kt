package com.example.invokables.apartments

import com.example.TelegramBot
import com.example.fetchingStrategies.avito.SaleAvitoFetchingStrategy
import com.example.fetchingStrategies.cian.SaleCianFetchingStrategy
import com.example.fetchingStrategies.ru09.SaleRu09FetchingStrategy
import com.example.persistent.DB
import com.example.services.geo.GeoService
import io.ktor.client.HttpClient
import services.geo.Boundary
import services.geo.Point

class SaleApartmentsAdParsersInvokable(tg: TelegramBot, http: HttpClient, gc: GeoService, db: DB) :
    ApartmentsAdParsersInvokable(tg, http, gc, db) {

    override var description = "Sale Apartment Ad WebSites Parser"
    override val groupChatId = -270950399L

    override val polygonOfInterest = Boundary(arrayOf(
        Point(56.4668584, 84.98375950000002),
        Point(56.4640134, 84.98933849999997),
        Point(56.4621641, 84.99251429999998),
        Point(56.4603147, 84.98951019999998),
        Point(56.4593662, 84.98745020000001),
        Point(56.4568526, 84.98238620000001),
        Point(56.4549081, 84.9756056),
        Point(56.453435, 84.97729809999998),
        Point(56.4517274, 84.97802760000002),
        Point(56.4513242, 84.97734100000002),
        Point(56.4516325, 84.97540980000002),
        Point(56.4531029, 84.97540980000002),
        Point(56.4537433, 84.97433690000003),
        Point(56.4540516, 84.9721912),
        Point(56.4550239, 84.97116119999998),
        Point(56.4552786, 84.96866820000002),
        Point(56.4553023, 84.95120170000001),
        Point(56.4715693, 84.95032579999997),
        Point(56.4716175, 84.9653758),
        Point(56.4690336, 84.96541869999999),
        Point(56.4689151, 84.9813833),
        Point(56.4669474, 84.98374360000003)
    ))

    override val fetchingStrategies = listOf(
        SaleCianFetchingStrategy(),
        SaleAvitoFetchingStrategy(),
        SaleRu09FetchingStrategy()
    )
}