// Archivo: model/Product.kt
package com.example.safebite.model

import com.google.gson.annotations.SerializedName

// 1. El objeto Producto
data class Product(
    @SerializedName("_id") val id: String = "",
    val product_name: String? = null,
    val brands: String? = null,
    val image_front_url: String? = null,
    val allergens_tags: List<String>? = null,
    val ingredients_text_es: String? = null,
    val nutriscore_grade: String? = null,
    val nova_group: Int? = null,
    val quantity: String? = null,

    // Campos para tu lógica
    val isFavorite: Boolean = false,
    val price: Double = 0.0,
    val store: String = "SafeBite Shop"
)

// 2. Respuesta para el escáner (un solo producto)
data class OpenFoodResponse(
    val status: Int,
    val product: Product?
)

// 3. ✅ ESTO ES LO QUE TE FALTA: Respuesta para la búsqueda (lista de productos)
data class OpenFoodSearchResponse(
    val products: List<Product> = emptyList()
)