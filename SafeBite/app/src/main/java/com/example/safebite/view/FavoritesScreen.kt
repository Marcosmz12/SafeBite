package com.example.safebite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safebite.R
import com.example.safebite.controller.ProductController
import com.example.safebite.model.Product
import com.example.safebite.ui.theme.GreenPrimary
import com.example.safebite.ui.theme.GreenSoft
import com.example.safebite.view.widgets.SafeBiteBottomBar
import com.example.safebite.view.widgets.SafeBiteTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavHostController,
    productController: ProductController,
    authController: com.example.safebite.controller.AuthController
) {
    val colors = MaterialTheme.colorScheme
    val favoritesList: List<Product> = productController.getFavorites()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            com.example.safebite.view.widgets.SafeBiteDrawerContent(
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
                // ── HEADER CON GRADIENTE ──────────────────────────────────────
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
                    Column {
                        Text(
                            stringResource(id = R.string.favorites_header),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = if (favoritesList.size == 1) {
                                stringResource(R.string.favorites_count_singular, favoritesList.size)
                            } else {
                                stringResource(R.string.favorites_count_plural, favoritesList.size)
                            },
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // ── CONTENIDO ──────────────────────────────────────────────────
                if (favoritesList.isEmpty()) {
                    EmptyFavoritesPlaceholder(navController)
                } else {
                    Text(
                        stringResource(id = R.string.title_favorites),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        color = colors.onBackground
                    )

                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = favoritesList) { product ->
                            ProductListItem(
                                product = product,
                                onFav = { productController.toggleFavorite(product.id) },
                                modifier = Modifier.clickable {
                                    productController.selectProduct(product)
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

@Composable
fun EmptyFavoritesPlaceholder(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(GreenSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.FavoriteBorder,
                    null,
                    tint = GreenPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(id = R.string.no_favorites_title), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                stringResource(id = R.string.no_favorites_desc),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = { navController.navigate("products") },
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GreenPrimary)
            ) {
                Text(stringResource(id = R.string.btn_explore), color = GreenPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}