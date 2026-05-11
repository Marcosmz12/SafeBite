package com.example.safebite.network

import com.example.safebite.model.OpenFoodResponse
import com.example.safebite.model.OpenFoodSearchResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface OpenFoodApi {
    @GET("api/v2/product/{barcode}.json?fields=product_name,brands,image_front_url,allergens_tags,ingredients_text_es,nutriscore_grade,nova_group,quantity,_id")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): OpenFoodResponse

    @GET("cgi/search.pl?action=process&json=1")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("fields") fields: String = "product_name,brands,image_front_url,allergens_tags,ingredients_text_es,nutriscore_grade,nova_group,quantity,_id"
    ): OpenFoodSearchResponse
}

object RetrofitClient {
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "SafeBite/1.0 (tu-email@ejemplo.com)")
                .build()
            chain.proceed(request)
        }
        .build()

    val openFoodApi: OpenFoodApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodApi::class.java)
    }
}