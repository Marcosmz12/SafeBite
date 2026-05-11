package com.example.safebite.view

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.safebite.R
import com.example.safebite.controller.AuthController
import com.example.safebite.controller.ProductController
import com.example.safebite.model.Product
import com.example.safebite.ui.theme.GreenPrimary
import com.example.safebite.ui.theme.GreenSoft
import com.example.safebite.view.widgets.SafeBiteBottomBar
import com.example.safebite.view.widgets.SafeBiteDrawerContent
import com.example.safebite.view.widgets.SafeBiteTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    navController: NavHostController,
    productController: ProductController,
    authController: AuthController
) {
    val colors = MaterialTheme.colorScheme
    var searchQuery by remember { mutableStateOf("") }

    val filterAll = stringResource(id = R.string.all_categories)
    val filterGluten = stringResource(id = R.string.cat_gluten_free)
    val filterLactose = stringResource(id = R.string.cat_lactose_free)
    val filterVegan = stringResource(id = R.string.cat_vegan)

    var selectedFilter by remember { mutableStateOf(filterAll) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedFilter) {
        productController.fetchProducts(searchQuery, selectedFilter)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SafeBiteDrawerContent(
                navController = navController,
                authController = authController,
                drawerState = drawerState,
                scope = scope
            )
        }
    ) {
        Scaffold(
            topBar = {
                SafeBiteTopBar(
                    title = stringResource(id = R.string.app_name),
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onProfileClick = {
                        navController.navigate("profile")
                    }
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

                // 1. HEADER CON BUSCADOR
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
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 12.dp),
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 15.sp, color = Color(0xFF1C1C1E)),
                                decorationBox = { inner ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            stringResource(id = R.string.search_hint),
                                            color = Color(0xFF9E9E9E),
                                            fontSize = 15.sp
                                        )
                                    }
                                    inner()
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GreenSoft)
                                    .clickable {
                                        productController.fetchProducts(
                                            searchQuery,
                                            selectedFilter
                                        )
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

                // 2. FILTROS
                val filters = listOf(filterAll, filterGluten, filterLactose, filterVegan)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
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
                            ) else null
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

                // 3. RESULTADOS
                Text(
                    text = if (productController.allProducts.isEmpty() && !productController.isLoading.value)
                        stringResource(id = R.string.no_products_found) else "Resultados para ti",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    color = colors.onBackground
                )

                // 4. LISTA DE PRODUCTOS
                Box(modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)) {
                    if (productController.isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = GreenPrimary
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(all = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(productController.allProducts) { product ->
                                ProductListItem(
                                    product = product,
                                    onFav = { productController.toggleFavorite(product.id) },
                                    modifier = Modifier.clickable {
                                        productController.selectProduct(product)
                                        navController.navigate("productDetail")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductListItem(product: Product, onFav: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.verticalGradient(listOf(GreenSoft, Color(0xFFDCEDC8)))),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.image_front_url,
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(R.drawable.logo_safebite)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    text = product.product_name ?: "Producto sin nombre",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.brands ?: product.store,
                    color = colors.onSurfaceVariant,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar NutriScore si existe
                if (product.nutriscore_grade != null) {
                    Text(
                        text = "Nutri-Score: ${product.nutriscore_grade.uppercase()}",
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                } else {
                    Text(
                        text = "${product.price} €",
                        color = GreenPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp
                    )
                }
            }

            IconButton(onClick = onFav) {
                Icon(
                    imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (product.isFavorite) Color(0xFFE91E63) else colors.onSurfaceVariant
                )
            }
        }
    }
}