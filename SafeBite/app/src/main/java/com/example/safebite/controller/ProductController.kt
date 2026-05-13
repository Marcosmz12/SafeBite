package com.example.safebite.controller

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safebite.model.Product
import com.example.safebite.model.FirestoreRepository
import com.example.safebite.network.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Locale

class ProductController : ViewModel() {
    private val firestoreRepo = FirestoreRepository()
    private val api = RetrofitClient.openFoodApi

    // ESTADOS REACTIVOS
    val allProducts = mutableStateListOf<Product>()
    val scannedProduct = mutableStateOf<Product?>(null)
    val selectedProduct = mutableStateOf<Product?>(null)
    val scanHistory = mutableStateListOf<Product>()
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    // Lista de IDs favoritos para comprobaciones rápidas
    private val favoriteIds = mutableStateListOf<String>()

    init {
        // ✅ Cargamos el historial y favoritos desde Firebase al arrancar
        loadHistoryFromFirebase()
    }

    fun loadHistoryFromFirebase() {
        viewModelScope.launch {
            try {
                // 1. Cargamos historial
                val history = firestoreRepo.getHistory()
                scanHistory.clear()
                scanHistory.addAll(history)

                // 2. Cargamos favoritos para sincronizar los corazones
                val favs = firestoreRepo.getFavorites()
                favoriteIds.clear()
                favoriteIds.addAll(favs.map { it.id })

                // Refrescamos la UI por si ya había productos cargados
                refreshFavoritesUI()

                println("Datos de Firebase cargados correctamente")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ✅ FUNCIÓN: Buscar productos (CORREGIDA)
    fun fetchProducts(query: String, filterKey: String) {
        val finalQuery = query.ifBlank { "comida" }
        val currentLang = AppCompatDelegate.getApplicationLocales().get(0)?.language
            ?: Locale.getDefault().language

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                // Seleccionamos el tipo de búsqueda según el filtro
                val response = when (filterKey) {
                    "gluten_free" -> api.searchProducts(
                        query = finalQuery,
                        lang = currentLang,
                        tag = "en:gluten"
                    )

                    "lactose_free" -> api.searchProducts(
                        query = finalQuery,
                        lang = currentLang,
                        tag = "en:milk"
                    )

                    "vegan" -> api.searchProducts(
                        query = finalQuery,
                        lang = currentLang,
                        tagType = "labels",
                        tag = "en:vegan"
                    )

                    else -> api.searchProducts(finalQuery, lang = currentLang)
                }

                allProducts.clear()
                response.products.let { products ->
                    // Aplicamos el estado de favorito de la lista que trajimos de Firebase
                    allProducts.addAll(products.map { it.copy(isFavorite = favoriteIds.contains(it.id)) })
                }
            } catch (e: Exception) {
                e.printStackTrace()
                error.value = "Error de conexión al buscar"
            } finally {
                isLoading.value = false
            }
        }
    }

    // ✅ FUNCIÓN: Escáner (Mantenida y Limpia)
    fun fetchProduct(barcode: String) {
        val currentLang = AppCompatDelegate.getApplicationLocales().get(0)?.language
            ?: Locale.getDefault().language

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val response = api.getProductByBarcode(barcode, lang = currentLang)
                response.product?.let { p ->
                    val product = p.copy(
                        id = barcode, // ID siempre es el código de barras
                        isFavorite = favoriteIds.contains(barcode)
                    )
                    scannedProduct.value = product

                    // Guardamos en historial si no es el último escaneado
                    if (scanHistory.none { it.id == product.id }) {
                        scanHistory.add(0, product)
                        firestoreRepo.addToHistory(product) // Guardado real en Firebase
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                error.value = "Producto no encontrado"
            } finally {
                isLoading.value = false
            }
        }
    }

    // ✅ FUNCIÓN: Favoritos (Sincronización Total)
    fun toggleFavorite(product: Product) {
        val productId = product.id
        if (productId.isEmpty()) return

        viewModelScope.launch {
            val isCurrentlyFav = favoriteIds.contains(productId)

            try {
                if (isCurrentlyFav) {
                    favoriteIds.remove(productId)
                    firestoreRepo.removeFavorite(productId)
                } else {
                    favoriteIds.add(productId)
                    val favProduct = product.copy(isFavorite = true)
                    firestoreRepo.addFavorite(favProduct)
                }

                // Actualizar todos los estados visuales
                refreshFavoritesUI()

            } catch (e: Exception) {
                println("Error en Favoritos: ${e.message}")
            }
        }
    }

    private fun refreshFavoritesUI() {
        // 1. Actualizar lista de búsqueda
        for (i in allProducts.indices) {
            allProducts[i] =
                allProducts[i].copy(isFavorite = favoriteIds.contains(allProducts[i].id))
        }
        // 2. Actualizar producto escaneado / detalle
        scannedProduct.value = scannedProduct.value?.copy(
            isFavorite = favoriteIds.contains(
                scannedProduct.value?.id ?: ""
            )
        )
        selectedProduct.value = selectedProduct.value?.copy(
            isFavorite = favoriteIds.contains(
                selectedProduct.value?.id ?: ""
            )
        )

        // 3. Actualizar historial visual
        for (i in scanHistory.indices) {
            scanHistory[i] =
                scanHistory[i].copy(isFavorite = favoriteIds.contains(scanHistory[i].id))
        }
    }

    fun selectProduct(product: Product) {
        selectedProduct.value = product
        scannedProduct.value = product
    }

    fun clearScanHistory() {
        scanHistory.clear()
        viewModelScope.launch { firestoreRepo.clearHistory() }
    }
}