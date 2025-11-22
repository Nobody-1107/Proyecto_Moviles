package com.example.proyectomoviles.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var baseUrl: String = "https://your.api.url/" // Cambia esta URL por la de tu backend

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun setBaseUrl(url: String) {
        baseUrl = url
    }
}
