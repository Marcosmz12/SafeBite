package com.example.safebite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // ESTE IMPORT ES EL QUE ARREGLA EL ERROR 'Int'
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safebite.controller.ProductController
import com.example.safebite.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavHostController, productController: ProductController) {
    val greenColor = Color(0xFF55AA33)
    // Llamada directa a la función del controlador
    val favoritesList: List<Product> = productController.getFavorites()

    Scaffold(
        bottomBar = { SafeBiteBottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFBFBFB))
        ) {
            Surface(
                color = greenColor,
                shape = RoundedCornerShape(bottomStart = 35.dp, bottomEnd = 35.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                    Text(
                        text = "Mis Favoritos",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                }
            }

            if (favoritesList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes favoritos", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp)
                ) {
                    // Usamos el parámetro 'items = ' explícitamente para evitar errores
                    items(items = favoritesList) { product ->
                        EnhancedProductItem(product = product) {
                            productController.toggleFavorite(product.id)
                        }
                    }
                }
            }
        }
    }
}