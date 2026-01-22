package com.example.safebite.view
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.safebite.controller.ProductController

@Composable
fun FavoritesScreen(navController: NavHostController, productController: ProductController) {
    Scaffold(bottomBar = { SafeBiteBottomBar(navController) }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp)) {
            Text("Mis Favoritos", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF55AA33))
            Spacer(Modifier.height(20.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                items(productController.getFavorites()) { product ->
                    ProductItem(product) { productController.toggleFavorite(product.id) }
                }
            }
        }
    }
}