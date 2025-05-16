package com.example.qrstudent

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// Interfaz para acceder a la API de Dog CEO usando Retrofit
interface APIServices {
    @GET("breed/{raza}/images")
    fun getDogsByBreeds(@Path("raza") breed: String): Call<DogsResponse>
}
