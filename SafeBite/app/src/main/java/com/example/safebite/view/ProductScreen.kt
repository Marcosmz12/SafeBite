package com.example.safebite.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safebite.R
import com.example.safebite.controller.ProductController
import com.example.safebite.model.Product

@Composable
fun ProductScreen(navController: NavHostController, productController: ProductController) {
    val greenColor = Color(0xFF55AA33)

    Scaffold(
        bottomBar = { SafeBiteBottomBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            // --- CABECERA VERDE ---
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                // Fondo curvo
                Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).background(greenColor))

                // Contenido de la cabecera
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 20.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.background(Color.White.copy(0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }

                    Text("Productos", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)

                    Image(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp).clip(CircleShape).border(2.dp, Color.White, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- LISTA DE PRODUCTOS (AQUÍ ESTÁ LO QUE FALTABA) ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                // items() recorre la lista del controlador
                items(productController.allProducts) { product ->
                    ProductItem(product = product) {
                        // Acción al pulsar el corazón
                        productController.toggleFavorite(product.id)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onFav: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(product.imageRes),
                contentDescription = null,
                modifier = Modifier.size(65.dp).clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Fit
            )

            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(product.store, color = Color.Gray, fontSize = 13.sp)
                Text("${product.price} €", color = Color(0xFFFFA500), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            IconButton(onClick = onFav) {
                Icon(
                    imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (product.isFavorite) Color.Red else Color.LightGray
                )
            }
        }
    }
}