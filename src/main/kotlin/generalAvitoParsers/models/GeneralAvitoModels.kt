package com.example.main.kotlin.generalAvitoParsers.models

import com.google.gson.annotations.SerializedName


data class GeneralAvitoResponse (
    val status: String,
    val result: GeneralAvitoResult
)

data class GeneralAvitoResult (
    val items: List<GeneralAvitoItem>
)

data class GeneralAvitoItem (
    val type: String,
    val value: GeneralAvitoValue
)

data class GeneralAvitoValue (
    val id: Any,
    val title: String,
    val price: String?,
    @SerializedName("uri_mweb")
    val uriMWeb: String
)