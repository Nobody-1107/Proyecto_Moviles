package com.example.proyectomoviles.network

object ApiHelper {
    val service: GenericApiService by lazy {
        ApiClient.retrofit.create(GenericApiService::class.java)
    }
}
