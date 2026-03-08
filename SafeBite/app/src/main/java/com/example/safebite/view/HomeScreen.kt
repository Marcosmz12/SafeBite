package com.example.safebite.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safebite.R
import com.example.safebite.controller.AuthController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, controller: AuthController) {
    val greenColor = Color(0xFF55AA33)
    val lightGreen = Color(0xFFF1F8E9)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SafeBite", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* Menú */ }) {
                        Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                    }
                },
                actions = {
                    // Reemplazamos redes sociales por el perfil del usuario
                    IconButton(onClick = { /* Perfil */ }) {
                        Icon(Icons.Outlined.AccountCircle, "Perfil", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = greenColor)
            )
        },
        bottomBar = { SafeBiteBottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER CON BIENVENIDA ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(colors = listOf(greenColor, Color.White)),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text("¡Hola, Gourmet!", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("¿Qué quieres comer seguro hoy?", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- BARRA DE BÚSQUEDA ---
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Busca productos o tiendas...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().background(Color.White, CircleShape),
                        shape = CircleShape,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            // --- CATEGORÍAS RÁPIDAS (Iconos circulares) ---
            Text(
                "Categorías",
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 10.dp),
                fontWeight = FontWeight.Bold, fontSize = 18.sp
            )
            val categories = listOf(
                "Sin Gluten" to Icons.Outlined.SetMeal,
                "Sin Lactosa" to Icons.Outlined.Egg,
                "Vegano" to Icons.Outlined.Eco,
                "Frutos Secos" to Icons.Outlined.BakeryDining
            )
            LazyRow(contentPadding = PaddingValues(horizontal = 15.dp)) {
                items(categories) { category ->
                    CategoryItem(category.first, category.second, greenColor)
                }
            }

            // --- PRODUCTOS DESTACADOS / OFERTAS ---
            Text(
                "Destacados cerca de ti",
                modifier = Modifier.padding(start = 20.dp, top = 25.dp, bottom = 10.dp),
                fontWeight = FontWeight.Bold, fontSize = 18.sp
            )
            LazyRow(contentPadding = PaddingValues(horizontal = 15.dp)) {
                items(3) { // Simulación de 3 tarjetas
                    ProductCard(greenColor)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- BOTÓN CERRAR SESIÓN (Más discreto) ---
            TextButton(
                onClick = {
                    controller.logout {
                        navController.navigate("login") { popUpTo("home") { inclusive = true } }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Outlined.Logout, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun CategoryItem(name: String, icon: ImageVector, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Surface(
            modifier = Modifier.size(65.dp),
            color = Color(0xFFF1F8E9),
            shape = CircleShape
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(18.dp))
        }
        Text(name, fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun ProductCard(color: Color) {
    Card(
        modifier = Modifier.width(200.dp).padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(120.dp).fillMaxWidth().background(Color.LightGray)) {
                // Aquí iría la imagen del producto
                Icon(Icons.Outlined.Image, null, Modifier.align(Alignment.Center), tint = Color.White)
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Pan Artesano", fontWeight = FontWeight.Bold)
                Text("Tienda Saludable", fontSize = 12.sp, color = Color.Gray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("3.50€", fontWeight = FontWeight.ExtraBold, color = color)
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.AddCircle, null, tint = color)
                    }
                }
            }
        }
    }
}

// Reutilizamos tu BottomBar pero con un diseño un poco más limpio
@Composable
fun SafeBiteBottomBar(navController: NavHostController) {
    val greenColor = Color(0xFF55AA33)
    Surface(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        color = greenColor,
        shape = CircleShape,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomBottomIcon(Icons.Outlined.Home) { navController.navigate("home") }
            CustomBottomIcon(Icons.Outlined.Restaurant) { navController.navigate("products") }
            CustomBottomIcon(Icons.Outlined.FavoriteBorder) { navController.navigate("favorites") }
            CustomBottomIcon(Icons.Outlined.ShoppingBag) { }
            CustomBottomIcon(Icons.Outlined.SupportAgent) { navController.navigate("chatbot")
            }
        }
    }
}

@Composable
fun CustomBottomIcon(icon: ImageVector, onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
    }
}