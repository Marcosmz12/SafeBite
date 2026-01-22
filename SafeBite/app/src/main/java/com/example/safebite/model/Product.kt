package com.example.safebite.model

data class Product(
    val id: Int,
    val name: String,
    val store: String,
    val price: Double,
    val imageRes: Int,
    val category: String,
    val isFavorite: Boolean = false
)