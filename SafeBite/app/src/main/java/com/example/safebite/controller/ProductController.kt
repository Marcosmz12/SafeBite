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
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.safebite.BuildConfig
import com.example.safebite.model.FirestoreRepository

// ── Spoonacular ───────────────────────────────────────────────────────────────
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

// ── Open Food Facts ───────────────────────────────────────────────────────────
data class OpenFoodResponse(
    val status: Int,
    val product: OpenFoodProduct?
)

// ✅ Añadidos allergens_tags e ingredients_text_es que faltaban
data class OpenFoodProduct(
    val product_name: String? = null,
    val brands: String? = null,
    val image_url: String? = null,
    val image_front_url: String? = null,
    val allergens_tags: List<String>? = null,
    val ingredients_text_es: String? = null,
    val nutriments: Map<String, Any>? = null
)

interface OpenFoodApi {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): OpenFoodResponse
}

// ── Controlador ───────────────────────────────────────────────────────────────
class ProductController : ViewModel() {
    private val apiKey = BuildConfig.SPOONACULAR_API_KEY
    private val firestoreRepo = FirestoreRepository()
    val allProducts = mutableStateListOf<Product>()
    private val favoriteIds = mutableListOf<Int>()
    val isLoading = mutableStateOf(false)
    val scannedProduct = mutableStateOf<Product?>(null)
    val error = mutableStateOf<String?>(null)
    val scanHistory = mutableStateListOf<Product>()

    private val spoonApi = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SpoonacularApi::class.java)

    private val openFoodApi = Retrofit.Builder()
        .baseUrl("https://world.openfoodfacts.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenFoodApi::class.java)

    // ✅ Solo UNA función fetchProduct (la duplicada era el error principal)
    fun fetchProduct(barcode: String) {
        // ✅ Normaliza el código a EAN-13
        val normalizedBarcode = when {
            barcode.length == 8 -> barcode.padStart(13, '0')  // EAN-8 → EAN-13
            barcode.length == 12 -> "0$barcode"                 // UPC-A → EAN-13
            else -> barcode
        }

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            scannedProduct.value = null
            try {
                val response = openFoodApi.getProductByBarcode(normalizedBarcode)
                if (response.status == 1 && response.product != null) {
                    val p = response.product
                    val product = Product(
                        id = normalizedBarcode.hashCode(),
                        name = p.product_name ?: "Sin nombre",
                        store = p.brands ?: "Marca desconocida",
                        price = 0.0,
                        imageUrl = p.image_front_url ?: p.image_url ?: "",
                        category = "Escaneado",
                        isFavorite = favoriteIds.contains(normalizedBarcode.hashCode()),
                        product_name = p.product_name,
                        brands = p.brands,
                        image_front_url = p.image_front_url ?: p.image_url,
                        allergens_tags = p.allergens_tags,
                        ingredients_text_es = p.ingredients_text_es
                    )
                    scannedProduct.value = product

                    // ✅ Añadir al historial
                    scanHistory.add(0, product)
                    if (scanHistory.size > 20) scanHistory.removeAt(scanHistory.lastIndex)
                    firestoreRepo.addToHistory(product)

                } else {
                    error.value = "Producto no encontrado: $normalizedBarcode"
                }
            } catch (e: Exception) {
                error.value = "Error de red: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

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
                val response = spoonApi.searchProducts(searchQuery, intolerance, apiKey)
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

    fun getFavorites(): List<Product> {
        return allProducts.filter { it.isFavorite }
    }

    fun clearScanHistory() {
        scanHistory.clear()
        viewModelScope.launch {
            firestoreRepo.clearHistory()
        }
    }

}