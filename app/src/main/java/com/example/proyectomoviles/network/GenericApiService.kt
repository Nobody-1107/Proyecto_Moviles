package com.example.proyectomoviles.network

import retrofit2.Call
import retrofit2.http.*

interface GenericApiService {
    @GET
    fun getRequest(@Url url: String): Call<Any>

    @POST
    fun postRequest(@Url url: String, @Body body: Any): Call<Any>

    @PUT
    fun putRequest(@Url url: String, @Body body: Any): Call<Any>

    @DELETE
    fun deleteRequest(@Url url: String): Call<Any>
}
