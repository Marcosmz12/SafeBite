package com.example.safebite.controller

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safebite.BuildConfig
import com.example.safebite.model.FirestoreRepository
import com.example.safebite.model.Product
import com.example.safebite.network.SpoonacularApi
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// ── Modelos para Open Food Facts (Barcode) ────────────────────────────────────
data class OpenFoodResponse(val status: Int, val product: OpenFoodProduct?)
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
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): OpenFoodResponse
}

class ProductController : ViewModel() {
    private val apiKey = BuildConfig.SPOONACULAR_API_KEY
    private val firestoreRepo = FirestoreRepository()

    // Estados
    val allProducts = mutableStateListOf<Product>()
    private val favoriteIds = mutableListOf<Int>()
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    val scannedProduct = mutableStateOf<Product?>(null)
    val scanHistory = mutableStateListOf<Product>()
    val selectedProduct = mutableStateOf<Product?>(null)

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

    fun selectProduct(product: Product) {
        selectedProduct.value = product
    }

    // ✅ Búsqueda con filtros (Spoonacular)
    fun fetchProducts(query: String, filter: String) {
        val searchQuery = if (query.isBlank()) "food" else query
        var intolerance: String? = null
        var diet: String? = null

        when (filter) {
            "Sin Gluten" -> intolerance = "gluten"
            "Sin Lactosa" -> intolerance = "dairy"
            "Vegano" -> diet = "vegan"
            else -> {
                intolerance = null; diet = null
            }
        }

        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = spoonApi.searchProducts(
                    query = searchQuery,
                    intolerances = intolerance,
                    diet = diet,
                    apiKey = apiKey
                )

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
                            isFavorite = favoriteIds.contains(spoonItem.id),
                            product_name = spoonItem.title,
                            image_front_url = spoonItem.image,
                            allergens_tags = if (filter != "Todo") listOf("es:$filter") else emptyList(),
                            ingredients_text_es = "Información disponible en tienda."
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

    // ✅ Búsqueda por código de barras (Open Food Facts)
    fun fetchProduct(barcode: String) {
        val normalizedBarcode = if (barcode.length == 8) barcode.padStart(13, '0')
        else if (barcode.length == 12) "0$barcode"
        else barcode

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
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
                    scanHistory.add(0, product)
                    firestoreRepo.addToHistory(product)
                }
            } catch (e: Exception) {
                error.value = "Error de red"
            } finally {
                isLoading.value = false
            }
        }
    }

    // ✅ GESTIÓN DE FAVORITOS (Restaurada)
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

    // ✅ LIMPIAR HISTORIAL (Corregida la sintaxis de corrutina)
    fun clearScanHistory() {
        scanHistory.clear()
        viewModelScope.launch {
            try {
                firestoreRepo.clearHistory()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}