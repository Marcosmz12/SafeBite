package com.example.safebite.model

data class Product(
    val id: Int,
    val name: String,
    val store: String,
    val price: Double,
    val imageRes: Int = 0,    // Para tus fotos locales (opcional)
    val imageUrl: String? = null, // PARA LA API
    val category: String,
    var isFavorite: Boolean = false
)