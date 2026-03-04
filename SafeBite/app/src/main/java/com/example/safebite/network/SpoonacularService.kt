package com.example.safebite.network

import com.example.safebite.model.SpoonacularResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularService {
    @GET("food/products/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("intolerances") intolerances: String, // Aquí pasarás "gluten" o "dairy"
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 10
    ): SpoonacularResponse
}