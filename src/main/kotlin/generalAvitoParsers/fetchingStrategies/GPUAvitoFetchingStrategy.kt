package com.example.main.kotlin.generalAvitoParsers.fetchingStrategies

import com.example.main.kotlin.apartmentsAdParsers.models.Room
import com.example.main.kotlin.generalAvitoParsers.interfaces.AvitoFetchingStrategy
import com.example.main.kotlin.generalAvitoParsers.models.AvitoAd
import com.example.main.kotlin.generalAvitoParsers.models.GeneralAvitoResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class GPUAvitoFetchingStrategy: AvitoFetchingStrategy {

    override val uniquePrefix: String
        get() = "_general_ad_avito"

    override fun fetchAds(http: OkHttpClient): List<AvitoAd> {
        val adsRequest = Request.Builder()
            .url("https://m.avito.ru/api/9/items?key=af0deccbgcgidddjgnvljitntccdduijhdinfgjgfjir&categoryId=101&params[483]=6581&locationId=657600&params[631]=6611&priceMin=10000&sort=default")
            .build();

        val httpResponse = http.newCall(adsRequest).execute();
        val avitoResponse = Gson().fromJson(httpResponse.body?.charStream(), GeneralAvitoResponse::class.java)

        httpResponse.body?.close()

        if (avitoResponse != null) {
            return avitoResponse.result.items.mapNotNull {
                if (it.value.price == null || it.type.toLowerCase().contains("item") == false) {
                    return@mapNotNull null
                }
                AvitoAd(
                    "${uniquePrefix}_${it.value.id.toString()}",
                    it.value.price,
                    it.value.title,
                    "https://avito.ru${it.value.uriMWeb}/"
                )
            }
        } else {
            return ArrayList<AvitoAd>()
        }
    }
}