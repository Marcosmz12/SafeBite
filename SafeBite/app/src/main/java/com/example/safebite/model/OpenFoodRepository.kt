// Archivo: model/OpenFoodRepository.kt
package com.example.safebite.model

import com.example.safebite.network.RetrofitClient

class OpenFoodRepository {
    private val api = RetrofitClient.openFoodApi

    // ✅ Usamos 'Product' en lugar de 'OpenFoodProduct'
    private val productCache = mutableMapOf<String, Product>()

    suspend fun getProductInfo(barcode: String): Product? {
        // 1. Mirar si ya está en la caché (Punto 5 de tu descubrimiento)
        if (productCache.containsKey(barcode)) {
            return productCache[barcode]
        }

        return try {
            // 2. Llamar a la función correcta de la API
            val response = api.getProductByBarcode(barcode)

            if (response.status == 1 && response.product != null) {
                // 3. Guardar en caché y devolver
                productCache[barcode] = response.product
                response.product
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}