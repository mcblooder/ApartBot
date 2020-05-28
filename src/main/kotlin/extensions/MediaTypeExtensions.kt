package com.example.main.kotlin.extensions

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

val MediaType.Companion.JSON: MediaType
    get() = "application/json; charset=utf-8".toMediaType()