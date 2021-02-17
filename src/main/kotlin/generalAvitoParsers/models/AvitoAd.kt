package com.example.main.kotlin.generalAvitoParsers.models

data class AvitoAd(
    val id: String,
    val price: String,
    var title: String,
    var url: String
) {
    val uniqueIdentity: String
        get() = id
}