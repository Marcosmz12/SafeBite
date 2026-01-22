package com.example.safebite.controller

import androidx.compose.runtime.mutableStateListOf
import com.example.safebite.R
import com.example.safebite.model.Product

class ProductController {
    // Lista observable: cuando cambia algo aquí, la UI se actualiza sola
    val allProducts = mutableStateListOf(
        Product(1, "Spaghetti sin gluten Gallo", "Alcampo", 2.33, R.drawable.spaghetti, "Gluten"),
        Product(2, "Pan de molde Schär", "DIA", 2.49, R.drawable.pan_molde, "Gluten"),
        Product(3, "Leche semi sin lactosa", "Carrefour", 1.25, R.drawable.leche, "Lactosa"),
        Product(4, "Queso Cheddar Arla", "Brit Store", 5.22, R.drawable.queso, "Lactosa"),
        Product(5, "Ketchup 0%", "MASmusculo", 3.50, R.drawable.ketchup, "Todos"),
        Product(6, "Azúcar Panela BIO", "Farmacia.bio", 3.50, R.drawable.azucar, "Todos")
    )

    // Función para marcar/desmarcar
    fun toggleFavorite(productId: Int) {
        val index = allProducts.indexOfFirst { it.id == productId }
        if (index != -1) {
            val product = allProducts[index]
            allProducts[index] = product.copy(isFavorite = !product.isFavorite)
        }
    }

    // Obtener solo los favoritos para la pantalla de Favoritos
    fun getFavorites() = allProducts.filter { it.isFavorite }
}