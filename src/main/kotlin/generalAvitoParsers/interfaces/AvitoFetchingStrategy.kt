package com.example.main.kotlin.generalAvitoParsers.interfaces

import com.example.main.kotlin.generalAvitoParsers.models.AvitoAd
import okhttp3.OkHttpClient

interface AvitoFetchingStrategy {
    val uniquePrefix: String
    fun fetchAds(http: OkHttpClient): List<AvitoAd>
}