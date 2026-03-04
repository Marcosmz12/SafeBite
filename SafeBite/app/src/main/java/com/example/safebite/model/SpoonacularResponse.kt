package com.example.safebite.model

// Lo que recibimos de la búsqueda de productos
data class SpoonacularResponse(
    val products: List<SpoonProduct>
)

data class SpoonProduct(
    val id: Int,
    val title: String,
    val image: String // URL de la imagen
)