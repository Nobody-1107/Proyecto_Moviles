package com.example.proyectomoviles.network

import com.example.proyectomoviles.model.Colaborador
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    // Endpoint correcto según tu Swagger: GET /api/Profiles/{id}
    // Nota: El ID ahora es un String (GUID), no un Int.
    @GET("api/Profiles/{id}")
    suspend fun getColaborador(@Path("id") id: String): Colaborador
}