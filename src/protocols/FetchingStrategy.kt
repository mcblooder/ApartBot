package com.example.Protocols

import com.example.Models.Room
import io.ktor.client.HttpClient

interface FetchingStrategy {
    val uniquePrefix: String
    fun fetchRooms(http: HttpClient): List<Room>
}