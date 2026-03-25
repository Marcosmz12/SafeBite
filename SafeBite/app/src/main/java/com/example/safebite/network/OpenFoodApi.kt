package com.example.safebite.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodApi {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): OpenFoodResponse

    @GET("cgi/search.pl?json=1&action=process&page_size=50&cc=es&lc=es")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("tagtype_0") tagType: String? = null,
        @Query("tag_contains_0") contains: String? = null,
        @Query("tag_0") tagValue: String? = null
    ): OpenFoodSearchResponse
}