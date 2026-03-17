package com.example.safebite.model

data class OpenFoodFactsResponse(
    val product: Product?,
    val status: Int // 1 si se encuentra, 0 si no
)
