package com.example.safebite.network

import com.example.safebite.model.OpenFoodFactsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApi {
    @GET("api/v2/product/{barcode}.json?lc=es")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): OpenFoodFactsResponse
}