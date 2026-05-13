package com.example.safebite.network

import com.example.safebite.model.OpenFoodResponse
import com.example.safebite.model.OpenFoodSearchResponse
import retrofit2.http.*

interface OpenFoodApi {

    // ✅ lc=es asegura ingredientes en español. cc=es prioriza productos de España.
    @GET("api/v2/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String,
        @Query("lc") lang: String = "es",
        @Query("cc") country: String = "es",
        @Query("fields") fields: String = "product_name,brands,image_front_url,allergens_tags,ingredients_text_es,nutriscore_grade,nova_group,quantity,_id"
    ): OpenFoodResponse

    // ✅ Búsqueda mejorada con lenguaje y país
    @GET("api/v2/search")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("lc") lang: String = "es",
        @Query("cc") country: String = "es",
        @Query("tagtype_0") tagType: String = "allergens",
        @Query("tag_contains_0") tagContains: String = "does_not_contain",
        @Query("tag_0") tag: String = "",
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 24, // ✅ Un número divisible por 2 y 3 para el diseño
        @Query("fields") fields: String = "product_name,brands,image_front_url,allergens_tags,labels_tags,nutriscore_grade,nova_group,_id"
    ): OpenFoodSearchResponse

    // ✅ EXTRA: Autocompletado de sugerencias (Muy útil para el buscador)
    @GET("https://uk.openfoodfacts.org/cgi/suggest.pl")
    suspend fun getSuggestions(
        @Query("term") term: String,
        @Query("lc") lang: String = "es"
    ): List<String>
}