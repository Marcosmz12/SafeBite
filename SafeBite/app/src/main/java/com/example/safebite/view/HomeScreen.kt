package com.example.safebite.view

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.safebite.controller.AuthController
import com.example.safebite.controller.ProductController
import com.example.safebite.model.Product

private val GreenPrimary = Color(0xFF2E7D32)
private val GreenLight   = Color(0xFF4CAF50)
val GreenSoft    = Color(0xFFE8F5E9)
private val AmberAccent  = Color(0xFFFFB300)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    controller: AuthController,
    productController: ProductController // ✅ nuevo parámetro
) {
    val colors = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val scanHistory = productController.scanHistory

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SafeBite",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, null, tint = Color.White)
                    }
                },
                actions = {
                    BadgedBox(
                        modifier = Modifier.padding(end = 16.dp),
                        badge = {
                            Badge(containerColor = AmberAccent) {
                                Text("3", fontSize = 10.sp, color = Color.Black)
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.Notifications, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.AccountCircle, null, tint = Color.White, modifier = Modifier.size(22.dp))
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
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() }
                .verticalScroll(rememberScrollState())
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
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Text(
                        "¡Hola, Gourmet! 👋",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        "Encuentra lo que puedes comer hoy",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // ── BANNER ESCANEAR ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(GreenPrimary, Color(0xFF66BB6A))))
                    .clickable { navController.navigate("scanner") }
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "🌿 Come seguro hoy",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Escanea cualquier producto\ny conoce sus alérgenos",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("Escanear", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // ── CATEGORÍAS ────────────────────────────────────────────────────
            SectionTitle("Categorías")

            val categories = listOf(
                Triple("Sin Gluten",   Icons.Outlined.SetMeal,     Color(0xFFF44336) to Color(0xFFFFEBEE)),
                Triple("Sin Lactosa",  Icons.Outlined.Egg,          Color(0xFF2196F3) to Color(0xFFE3F2FD)),
                Triple("Vegano",       Icons.Outlined.Eco,           Color(0xFF4CAF50) to Color(0xFFE8F5E9)),
                Triple("Frutos Secos", Icons.Outlined.BakeryDining,  Color(0xFFFF9800) to Color(0xFFFFF3E0))
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { (name, icon, colorPair) ->
                    CategoryChip(name, icon, colorPair.first, colorPair.second)
                }
            }

            // ── HISTORIAL DE ESCANEOS ─────────────────────────────────────────
            if (scanHistory.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Escaneados recientemente",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        letterSpacing = (-0.3).sp,
                        color = colors.onBackground
                    )
                    TextButton(onClick = { productController.clearScanHistory() }) {
                        Text("Limpiar", color = colors.outline, fontSize = 12.sp)
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(scanHistory) { product ->
                        ScannedProductCard(
                            product = product,
                            onClick = {
                                navController.navigate("productDetail/${product.id}")
                            }
                        )
                    }
                }
            } else {
                // Estado vacío
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surfaceVariant)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.QrCodeScanner,
                            null,
                            tint = colors.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Aún no has escaneado ningún producto",
                            color = colors.onSurfaceVariant,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { navController.navigate("scanner") },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenPrimary),
                            border = BorderStroke(1.5.dp, GreenPrimary)
                        ) {
                            Icon(Icons.Outlined.QrCodeScanner, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Escanear ahora", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ── ACCESO RÁPIDO ─────────────────────────────────────────────────
            SectionTitle("Acceso rápido")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAccessCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.FavoriteBorder,
                    title = "Favoritos",
                    subtitle = "Tus productos guardados",
                    color = Color(0xFFE91E63),
                    onClick = { navController.navigate("favorites") }
                )
                QuickAccessCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.SupportAgent,
                    title = "ChatBot",
                    subtitle = "Consulta dudas al instante",
                    color = Color(0xFF9C27B0),
                    onClick = { navController.navigate("chatbot") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── CERRAR SESIÓN ─────────────────────────────────────────────────
            TextButton(
                onClick = {
                    controller.logout {
                        navController.navigate("login") { popUpTo("home") { inclusive = true } }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Outlined.Logout, null, tint = colors.outline, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cerrar Sesión", color = colors.outline, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ── CARD DE PRODUCTO ESCANEADO ────────────────────────────────────────────────
@Composable
fun ScannedProductCard(product: Product, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() }
            .shadow(3.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(GreenSoft, Color(0xFFDCEDC8)))),
                contentAlignment = Alignment.Center
            ) {
                if (!product.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Outlined.Image,
                        null,
                        tint = GreenPrimary.copy(alpha = 0.4f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    product.product_name ?: product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = colors.onSurface,
                    lineHeight = 17.sp
                )
                Text(
                    product.brands ?: product.store,
                    fontSize = 11.sp,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

// ── RESTO DE COMPONENTES (sin cambios) ────────────────────────────────────────

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        letterSpacing = (-0.3).sp,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun CategoryChip(name: String, icon: ImageVector, iconColor: Color, bgColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(28.dp))
        }
        Text(
            name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 6.dp),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
fun QuickAccessCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp)
        }
    }
}

@Composable
fun SafeBiteBottomBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    Surface(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .fillMaxWidth(),
        color = GreenPrimary,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Outlined.Home,          "home",      currentRoute) { navController.navigate("home") }
            BottomNavItem(Icons.Outlined.Restaurant,    "products",  currentRoute) { navController.navigate("products") }
            BottomNavItem(Icons.Outlined.FavoriteBorder,"favorites", currentRoute) { navController.navigate("favorites") }
            BottomNavItem(Icons.Outlined.ShoppingBag,   "shop",      currentRoute) { }
            BottomNavItem(Icons.Outlined.SupportAgent,  "chatbot",   currentRoute) { navController.navigate("chatbot") }
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, route: String, currentRoute: String?, onClick: () -> Unit) {
    val isActive = currentRoute == route
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) Color.White.copy(alpha = 0.2f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon, null,
            tint = if (isActive) Color.White else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
    }
}