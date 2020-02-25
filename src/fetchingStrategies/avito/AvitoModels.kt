package com.example.fetchingStrategies.avito

import com.google.gson.annotations.SerializedName

data class AvitoResponse (
    val status: String,
    val result: AvitoResult
)

data class AvitoResult (
    val items: List<AvitoItem>
)

data class AvitoItem (
    val type: String,
    val value: AvitoValue
)

data class AvitoValue (
    val id: Long,
    val address: String?,
    val coords: AvitoCoords?,
    val title: String,
    val price: String?,
    @SerializedName("uri_mweb")
    val uriMWeb: String,
    val callAction: AvitoCallAction? = null
)

data class AvitoCallAction (
    val uri: String,
    val title: String,
    val type: String
)

data class AvitoCoords (
    val lat: String?,
    val lng: String?
)