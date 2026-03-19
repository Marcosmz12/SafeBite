package com.example.safebite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safebite.controller.ProductController
import com.example.safebite.model.Product
import com.example.safebite.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavHostController, productController: ProductController) {
    val colors = MaterialTheme.colorScheme
    val favoritesList: List<Product> = productController.getFavorites()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis Favoritos",
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
            // ── HEADER ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(GreenPrimary, GreenPrimary.copy(alpha = 0.85f), colors.background)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            "❤️ Guardados",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            "${favoritesList.size} producto${if (favoritesList.size != 1) "s" else ""} guardado${if (favoritesList.size != 1) "s" else ""}",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // ── CONTENIDO ─────────────────────────────────────────────────────
            if (favoritesList.isEmpty()) {
                // Estado vacío — mismo estilo que el historial vacío en Home
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
                        Text(
                            "Aún no tienes favoritos",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = colors.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Guarda productos desde\nla pantalla de explorar",
                            color = colors.onSurfaceVariant,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        OutlinedButton(
                            onClick = { navController.navigate("products") },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenPrimary),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, GreenPrimary)
                        ) {
                            Text("Explorar productos", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Contador + lista
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tus guardados",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        letterSpacing = (-0.3).sp,
                        color = colors.onBackground
                    )
                    Text(
                        "${favoritesList.size} productos",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 4.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = favoritesList) { product ->
                        // ✅ Reutiliza exactamente el mismo componente que ProductScreen
                        ProductListItem(
                            product = product,
                            onFav = { productController.toggleFavorite(product.id) }
                        )
                    }
                }
            }
        }
    }
}