package com.example.safebite.controller

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safebite.model.Product
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Clases de respuesta
data class SpoonacularResponse(val products: List<SpoonProduct>)
data class SpoonProduct(val id: Int, val title: String, val image: String)

interface SpoonacularApi {
    @GET("food/products/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("intolerances") intolerances: String,
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 15
    ): SpoonacularResponse
}

class ProductController : ViewModel() {
    private val apiKey = "740a712a3daf41eb80d9ca6bd689e86a" // <--- PON TU CLAVE AQUÍ

    val allProducts = mutableStateListOf<Product>()
    private val favoriteIds = mutableListOf<Int>()
    val isLoading = mutableStateOf(false)

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(SpoonacularApi::class.java)

    fun fetchProducts(query: String, filter: String) {
        val searchQuery = if (query.isBlank()) "food" else query
        val intolerance = when (filter) {
            "Sin Gluten" -> "gluten"
            "Sin Lactosa" -> "dairy"
            "Vegano" -> "vegan"
            else -> ""
        }

        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = api.searchProducts(searchQuery, intolerance, apiKey)
                allProducts.clear()
                response.products.forEach { spoonItem ->
                    allProducts.add(
                        Product(
                            id = spoonItem.id,
                            name = spoonItem.title,
                            store = "SafeBite Shop",
                            price = 2.99,
                            imageUrl = spoonItem.image,
                            category = filter,
                            isFavorite = favoriteIds.contains(spoonItem.id)
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun toggleFavorite(productId: Int) {
        val index = allProducts.indexOfFirst { it.id == productId }
        if (favoriteIds.contains(productId)) {
            favoriteIds.remove(productId)
            if (index != -1) allProducts[index] = allProducts[index].copy(isFavorite = false)
        } else {
            favoriteIds.add(productId)
            if (index != -1) allProducts[index] = allProducts[index].copy(isFavorite = true)
        }
    }

    // COMPRUEBA QUE ESTA FUNCIÓN ESTÉ AQUÍ DENTRO
    fun getFavorites(): List<Product> {
        return allProducts.filter { it.isFavorite }
    }
} // <--- ESTA ES LA ÚLTIMA LLAVE DE LA CLASE