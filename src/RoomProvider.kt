package com.example

import io.ktor.client.HttpClient

interface RoomProvider {
    val uniquePrefix: String
    fun fetchRooms(http: HttpClient): List<Room>
}