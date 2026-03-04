package com.example.safebite.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.safebite.R
import com.example.safebite.controller.ProductController
import com.example.safebite.model.Product
import androidx.compose.runtime.LaunchedEffect // Para arreglar el error de LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator // Para arreglar CircularProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(navController: NavHostController, productController: ProductController) {
    val greenColor = Color(0xFF55AA33)

    // --- ESTADOS LOCALES ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todo") }

    // --- EFECTO INICIAL Y DE BÚSQUEDA ---
    // Cada vez que cambie el filtro o la búsqueda, llamamos a la API
    // Usamos LaunchedEffect para no saturar la API en cada pulsación (podemos poner un delay si es necesario)
    LaunchedEffect(selectedFilter) {
        productController.fetchProducts(searchQuery, selectedFilter)
    }

    Scaffold(
        bottomBar = { SafeBiteBottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFBFBFB))
        ) {
            // --- CABECERA ---
            Surface(
                color = greenColor,
                shape = RoundedCornerShape(bottomStart = 35.dp, bottomEnd = 35.dp),
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(bottom = 25.dp, start = 10.dp, end = 10.dp, top = 10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, "Atrás", tint = Color.White)
                        }
                        Text("Explorar", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color.White)
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(0.2f))) {
                            Image(
                                painter = painterResource(id = R.drawable.avatar),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // --- BARRA DE BÚSQUEDA ---
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("¿Qué producto buscas?", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = greenColor) },
                        trailingIcon = {
                            // Botón para ejecutar la búsqueda manual
                            IconButton(onClick = { productController.fetchProducts(searchQuery, selectedFilter) }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = greenColor)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp).shadow(4.dp, CircleShape),
                        shape = CircleShape,
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            // --- FILTROS ---
            val filters = listOf("Todo", "Sin Gluten", "Sin Lactosa", "Vegano")
            LazyRow(
                modifier = Modifier.padding(vertical = 15.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filters) { filter ->
                    FilterChipSafe(
                        text = filter,
                        isSelected = selectedFilter == filter,
                        color = greenColor,
                        onClick = {
                            selectedFilter = filter
                            // Al cambiar el filtro, la API se llama automáticamente por el LaunchedEffect
                        }
                    )
                }
            }

            // --- CONTENIDO PRINCIPAL ---
            Box(modifier = Modifier.fillMaxSize()) {
                if (productController.isLoading.value) {
                    // Muestra cargador mientras la API responde
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = greenColor
                    )
                } else {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text(
                            text = if (productController.allProducts.isEmpty()) "No hay resultados" else "Resultados para ti",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(productController.allProducts) { product ->
                                EnhancedProductItem(product = product) {
                                    productController.toggleFavorite(product.id)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipSafe(text: String, isSelected: Boolean, color: Color, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) color else Color.White,
        shape = CircleShape,
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        border = BorderStroke(1.dp, if (isSelected) color else Color(0xFFEEEEEE)),
        modifier = Modifier.clickable { onClick() } // AHORA SÍ DETECTA EL CLICK
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.DarkGray,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun EnhancedProductItem(product: Product, onFav: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen con fondo suave para que resalte
            Box(
                modifier = Modifier
                    .size(85.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.imageUrl, // Ahora usamos la URL de Spoonacular
                    contentDescription = product.name,
                    modifier = Modifier.size(70.dp).clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(R.drawable.logo_safebite)
                )
            }

            Column(modifier = Modifier
                .weight(1f)
                .padding(horizontal = 15.dp)) {
                Text(
                    product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                )
                Text(product.store, color = Color.Gray, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${product.price} €",
                        color = Color(0xFF55AA33),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    // Pequeño indicador de "Disponible" o "Seguro"
                    Surface(color = Color(0xFFE8F5E9), shape = CircleShape) {
                        Text(
                            "Seguro",
                            color = Color(0xFF2E7D32),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Botón de favorito estilizado
            IconButton(
                onClick = onFav,
                modifier = Modifier.background(
                    if (product.isFavorite) Color(0xFFFFEBEE) else Color.Transparent,
                    CircleShape
                )
            ) {
                Icon(
                    imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (product.isFavorite) Color.Red else Color.LightGray,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}