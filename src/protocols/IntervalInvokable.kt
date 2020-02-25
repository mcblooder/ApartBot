package com.example.protocols

interface IntervalInvokable {
    val interval: Int
    val description: String
    fun invoke()
}