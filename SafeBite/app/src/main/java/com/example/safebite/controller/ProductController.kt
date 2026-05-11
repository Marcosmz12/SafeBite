package com.example.safebite.controller

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safebite.model.Product
import com.example.safebite.model.FirestoreRepository // ✅ Importante
import com.example.safebite.network.RetrofitClient
import kotlinx.coroutines.launch

class ProductController : ViewModel() {
    // 1. Declaramos la conexión a Firebase
    private val firestoreRepo = FirestoreRepository()

    // 2. Declaramos la conexión a la API de Open Food Facts
    private val api = RetrofitClient.openFoodApi

    // ESTADOS PARA LA VISTA
    val allProducts = mutableStateListOf<Product>()
    val scannedProduct = mutableStateOf<Product?>(null)
    val scanHistory = mutableStateListOf<Product>()
    val isLoading = mutableStateOf(false)
    private val favoriteIds = mutableListOf<String>()

    // FUNCIÓN: Buscar productos por nombre (Lista)
    // Dentro de ProductController.kt

    fun fetchProducts(query: String, filter: String) {
        // ✅ CAMBIO: Si está vacío, buscamos algo por defecto para que la pantalla no esté vacía
        val finalQuery = if (query.isBlank()) "comida" else query

        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = api.searchProducts(finalQuery)
                val results = response.products ?: emptyList()

                val filtered = when (filter) {
                    "Sin Gluten" -> results.filter { !it.allergens_tags.orEmpty().contains("en:gluten") }
                    "Sin Lactosa" -> results.filter { !it.allergens_tags.orEmpty().contains("en:milk") }
                    "Vegano" -> results.filter { it.allergens_tags.orEmpty().contains("en:vegan") }
                    else -> results
                }

                allProducts.clear()
                allProducts.addAll(filtered.map { it.copy(isFavorite = favoriteIds.contains(it.id)) })
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun fetchProduct(barcode: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = api.getProductByBarcode(barcode)
                response.product?.let { p ->
                    val product = p.copy(isFavorite = favoriteIds.contains(p.id))
                    scannedProduct.value = product

                    // ✅ IMPORTANTE: Añadir al historial aquí
                    // Evitamos duplicados seguidos
                    if (scanHistory.isEmpty() || scanHistory.first().id != product.id) {
                        scanHistory.add(0, product)
                        firestoreRepo.addToHistory(product)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    // FUNCIÓN: Borrar historial (Visual y en Firebase)
    fun clearScanHistory() {
        scanHistory.clear() // Borrado visual
        viewModelScope.launch {
            try {
                // ✅ Ahora sí reconocerá firestoreRepo
                firestoreRepo.clearHistory()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // FUNCIÓN: Marcar/Desmarcar Favorito
    fun toggleFavorite(productId: String) {
        if (favoriteIds.contains(productId)) favoriteIds.remove(productId) else favoriteIds.add(
            productId
        )

        // Actualizamos la UI
        val index = allProducts.indexOfFirst { it.id == productId }
        if (index != -1) allProducts[index] =
            allProducts[index].copy(isFavorite = favoriteIds.contains(productId))

        if (scannedProduct.value?.id == productId) {
            scannedProduct.value =
                scannedProduct.value?.copy(isFavorite = favoriteIds.contains(productId))
        }
    }

    fun selectProduct(product: Product) {
        scannedProduct.value = product
    }
}