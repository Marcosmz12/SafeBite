package com.example.safebite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safebite.controller.ProductController

@Composable
fun FavoritesScreen(navController: NavHostController, productController: ProductController) {
    val greenColor = Color(0xFF55AA33)
    val favoriteProducts = productController.getFavorites()

    Scaffold(
        bottomBar = { SafeBiteBottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5)) // Fondo gris claro para que resalten las tarjetas blancas
        ) {
            // --- CABECERA ESTILO SAFEBITE ---
            Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                // Fondo Verde Curvo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 35.dp, bottomEnd = 35.dp))
                        .background(greenColor)
                )

                // Título y Botón Volver
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp, start = 20.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.background(Color.White.copy(0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(15.dp))

                    Text(
                        text = "Mis Favoritos",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- CONTENIDO ---
            if (favoriteProducts.isEmpty()) {
                // ESTADO VACÍO: Si no hay favoritos, mostramos este diseño
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = Color.LightGray.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Aún no tienes favoritos",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Explora productos y pulsa el corazón",
                        fontSize = 14.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // LISTA DE FAVORITOS
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(favoriteProducts) { product ->
                        // Reutilizamos el ProductItem de la pantalla de productos
                        ProductItem(product = product) {
                            productController.toggleFavorite(product.id)
                        }
                    }
                }
            }
        }
    }
}