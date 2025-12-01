package com.example.proyectomoviles.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5181/" // URL para el emulador

    val instance: Retrofit by lazy {
        // Creamos un interceptor de logging para ver las peticiones y respuestas
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Creamos un cliente OkHttp y le añadimos el interceptor
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient) // Usamos el cliente con logging
            .build()
    }
}