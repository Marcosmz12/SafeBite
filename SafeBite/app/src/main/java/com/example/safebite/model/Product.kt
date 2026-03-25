package com.example.safebite.model

data class Product(
    val id: Int,
    val name: String,
    val store: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val isFavorite: Boolean = false,
    // CAMPOS EXTRA PARA EL DETALLE (Asegúrate de que estén aquí)
    val product_name: String? = null,
    val brands: String? = null,
    val image_front_url: String? = null,
    val allergens_tags: List<String>? = null,
    val ingredients_text_es: String? = null
)