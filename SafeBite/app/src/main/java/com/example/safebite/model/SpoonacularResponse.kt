package com.example.safebite.model

// 1. Para el listado de búsqueda
data class SpoonacularSearchResponse(
    val products: List<SpoonProduct>
)

data class SpoonProduct(
    val id: Int,
    val title: String,
    val image: String
)

// 2. Para el detalle profundo (el que ya tenías)
data class SpoonacularDetailResponse(
    val id: Int,
    val title: String,
    val ingredientList: String?,
    val nutrition: Nutrition?
)

data class Nutrition(
    val flaws: List<String>?,
    val ingredients: List<SpoonIngredient>?
)

data class SpoonIngredient(
    val name: String,
    val safety_level: String?
)