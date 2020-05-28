package com.example.main.kotlin.apartmentsAdParsers.interfaces

import com.example.main.kotlin.apartmentsAdParsers.models.Room
import okhttp3.OkHttpClient

interface FetchingStrategy {
    val uniquePrefix: String
    fun fetchRooms(http: OkHttpClient): List<Room>
}