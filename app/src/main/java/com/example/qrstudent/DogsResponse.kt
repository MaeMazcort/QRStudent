package com.example.qrstudent

import com.google.gson.annotations.SerializedName

// Modelo de datos para mapear la respuesta JSON de la API
data class DogsResponse(
    @SerializedName("message") val images: List<String>,
    @SerializedName("status") val status: String
)
