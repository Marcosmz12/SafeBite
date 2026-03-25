package com.example.safebite.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.safebite.R
import com.example.safebite.controller.ProductController
import com.example.safebite.model.Product
import com.example.safebite.ui.theme.GreenPrimary
import com.example.safebite.ui.theme.GreenSoft

// ── Misma paleta que HomeScreen ───────────────────────────────────────────────


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(navController: NavHostController, productController: ProductController) {
    val colors = MaterialTheme.colorScheme
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todo") }

    LaunchedEffect(selectedFilter) {
        productController.fetchProducts(searchQuery, selectedFilter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Explorar",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBackIosNew, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.FilterList, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
            )
        },
        bottomBar = { SafeBiteBottomBar(navController) },
        containerColor = colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colors.background)
        ) {

            // ── HEADER CON BUSCADOR ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                GreenPrimary,
                                GreenPrimary.copy(alpha = 0.85f),
                                colors.background
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search,
                            null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 12.dp),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 15.sp,
                                color = Color(0xFF1C1C1E)
                            ),
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        "¿Qué producto buscas?",
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 15.sp
                                    )
                                }
                                inner()
                            },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                imeAction = androidx.compose.ui.text.input.ImeAction.Search
                            ),
                            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                                onSearch = {
                                    productController.fetchProducts(searchQuery, selectedFilter)
                                }
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GreenSoft)
                                .clickable {
                                    productController.fetchProducts(searchQuery, selectedFilter)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Search,
                                null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // ── FILTROS ───────────────────────────────────────────────────────
            val filters = listOf("Todo", "Sin Gluten", "Sin Lactosa", "Vegano")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        modifier = Modifier.clickable { selectedFilter = filter },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) GreenPrimary else colors.surfaceVariant,
                        border = if (!isSelected) BorderStroke(
                            1.dp,
                            colors.outline.copy(alpha = 0.2f)
                        ) else null,
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else colors.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // ── RESULTADOS ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (productController.allProducts.isEmpty() && !productController.isLoading.value)
                        "Sin resultados" else "Resultados para ti",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    letterSpacing = (-0.3).sp,
                    color = colors.onBackground
                )
                if (productController.allProducts.isNotEmpty()) {
                    Text(
                        "${productController.allProducts.size} productos",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            // ── LOADING / LISTA ───────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxSize()) {
                if (productController.isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = GreenPrimary,
                        strokeWidth = 3.dp
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 8.dp,
                            bottom = 100.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Dentro de ProductScreen.kt (en el LazyColumn)
                        items(productController.allProducts) { product ->
                            ProductListItem(
                                product = product,
                                onFav = { productController.toggleFavorite(product.id) },
                                modifier = Modifier.clickable {
                                    // 1. Guardamos el producto en el controlador
                                    productController.selectProduct(product)
                                    // 2. Navegamos a la ruta GENERAL (la que no necesita cargar nada de internet)
                                    navController.navigate("product_detail_general")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── CARD DE PRODUCTO ──────────────────────────────────────────────────────────
@Composable
fun ProductListItem(product: Product, onFav: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = modifier // Aplicamos el modifier aquí
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Imagen ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(listOf(GreenSoft, Color(0xFFDCEDC8)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(R.drawable.logo_safebite)
                )
            }

            // ── Info ──────────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    product.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    product.store,
                    color = colors.onSurfaceVariant,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${product.price} €",
                        color = GreenPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GreenSoft)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "✓ Seguro",
                            color = GreenPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ── Favorito ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (product.isFavorite) Color(0xFFFFEBEE)
                        else colors.outline.copy(alpha = 0.08f)
                    )
                    .clickable { onFav() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (product.isFavorite) Icons.Default.Favorite
                    else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (product.isFavorite) Color(0xFFE91E63)
                    else colors.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}