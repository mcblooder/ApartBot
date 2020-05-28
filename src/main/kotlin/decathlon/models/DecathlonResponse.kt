package com.example.main.kotlin.decathlon.models

data class DecathlonResponse (
    val id: String,
    val trackNum: String? = null,
    val estimatedShipmentDate: String,
    val estimatedDeliveryDate: String,
    val orderStatusHistory: List<OrderStatusHistory>
)

data class Status (
    val id: Long,
    val code: Long,
    val status: String
)

data class OrderStatusHistory (
    val id: Long,
    val status: Status,
    val date: String,
    val createdAt: String,
    val transportStatus: String
) {
    val uniqueIdentity: String
        get() = "decathlon_${id}"
}