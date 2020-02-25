package com.example.protocols

import com.example.models.Room
import io.ktor.client.HttpClient

interface FetchingStrategy {
    val uniquePrefix: String
    fun fetchRooms(http: HttpClient): List<Room>
}