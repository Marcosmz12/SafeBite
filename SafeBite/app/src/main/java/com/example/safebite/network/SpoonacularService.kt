package com.example.safebite.network

import com.example.safebite.model.SpoonacularSearchResponse
import com.example.safebite.model.SpoonacularDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApi {
    @GET("food/products/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("intolerances") intolerances: String?,
        @Query("diet") diet: String?,
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 15
    ): SpoonacularSearchResponse // Devuelve la lista

    @GET("food/products/{id}")
    suspend fun getProductDetails(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): SpoonacularDetailResponse // Devuelve el detalle único
}