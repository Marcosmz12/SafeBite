package com.example.safebite.model

data class Product(
    // ── Campos originales (Spoonacular / búsqueda por texto) ──
    val id: Int = 0,
    val name: String = "",
    val store: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",
    val isFavorite: Boolean = false,

    // ✅ Campos de Open Food Facts (escáner de código de barras)
    val product_name: String? = null,
    val brands: String? = null,
    val image_front_url: String? = null,
    val allergens_tags: List<String>? = null,
    val ingredients_text_es: String? = null
)