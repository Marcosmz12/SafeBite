package com.example.safebite.view

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalFocusManager
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
    val colors = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current // Para cerrar el teclado del buscador
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("SafeBite", color = colors.onPrimary, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { /* Abrir Drawer o Menú */ }) {
                        Icon(Icons.Default.Menu, "Menu", tint = colors.onPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Ir a Perfil */ }) {
                        Icon(
                            Icons.Outlined.AccountCircle,
                            "Perfil",
                            tint = colors.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.primary)
            )
        },
        bottomBar = { SafeBiteBottomBar(navController) },
        containerColor = colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // Al tocar el fondo se cierra el teclado de búsqueda
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() }
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER CON BIENVENIDA ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(colors.primary, colors.background)
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        "¡Hola, Gourmet!",
                        color = colors.onPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "¿Qué quieres comer seguro hoy?",
                        color = colors.onPrimary.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- BARRA DE BÚSQUEDA ADAPTABLE ---
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Busca productos...", color = colors.onSurfaceVariant)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, null, tint = colors.primary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surface, CircleShape),
                        shape = CircleShape,
                        singleLine = true,
                        // Acción de búsqueda en teclado
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Search
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onSearch = { focusManager.clearFocus() }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.onSurface,
                            unfocusedTextColor = colors.onSurface,
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = Color.Transparent, // Sin borde para efecto "pill" limpio
                            focusedContainerColor = colors.surface,
                            unfocusedContainerColor = colors.surface
                        )
                    )
                }
            }

            // --- SECCIÓN CATEGORÍAS ---
            SectionTitle("Categorías")

            val categories = listOf(
                "Sin Gluten" to Icons.Outlined.SetMeal,
                "Sin Lactosa" to Icons.Outlined.Egg,
                "Vegano" to Icons.Outlined.Eco,
                "Frutos Secos" to Icons.Outlined.BakeryDining
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    CategoryItem(category.first, category.second)
                }
            }

            // --- SECCIÓN PRODUCTOS ---
            SectionTitle("Destacados cerca de ti")

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(3) { ProductCard() }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- BOTÓN CERRAR SESIÓN (Adaptado) ---
            TextButton(
                onClick = {
                    controller.logout {
                        navController.navigate("login") { popUpTo("home") { inclusive = true } }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Outlined.Logout, null, tint = colors.outline, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", color = colors.outline, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 20.dp, top = 25.dp, bottom = 12.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun CategoryItem(name: String, icon: ImageVector) {
    val colors = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(68.dp),
            color = colors.outline, // Color suave dinámico
            shape = CircleShape,
            tonalElevation = 2.dp
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = colors.onPrimaryContainer,
                modifier = Modifier.padding(18.dp)
            )
        }
        Text(
            name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onBackground,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
fun ProductCard() {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.width(210.dp),
        shape = RoundedCornerShape(20.dp),
        // surfaceVariant ayuda a separar la tarjeta del fondo en modo oscuro
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth()
                    .background(colors.outlineVariant.copy(alpha = 0.5f))
            ) {
                Icon(
                    Icons.Outlined.Image,
                    null,
                    Modifier.size(40.dp).align(Alignment.Center),
                    tint = colors.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "Pan Artesano",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.onSurface
                )
                Text(
                    "Tienda Saludable",
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "3.50€",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = colors.primary
                    )
                    IconButton(
                        onClick = { /* Añadir al carrito */ },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Outlined.Add, null, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SafeBiteBottomBar(navController: NavHostController) {
    val colors = MaterialTheme.colorScheme
    // Barra flotante moderna
    Surface(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 15.dp)
            .fillMaxWidth(),
        color = colors.primary,
        shape = CircleShape,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomBottomIcon(Icons.Outlined.Home, true) { navController.navigate("home") }
            CustomBottomIcon(Icons.Outlined.Restaurant) { navController.navigate("products") }
            CustomBottomIcon(Icons.Outlined.FavoriteBorder) { navController.navigate("favorites") }
            CustomBottomIcon(Icons.Outlined.ShoppingBag) { }
            CustomBottomIcon(Icons.Outlined.SupportAgent) { navController.navigate("chatbot") }
        }
    }
}

@Composable
fun CustomBottomIcon(icon: ImageVector, isSelected: Boolean = false, onClick: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            // Si está seleccionado, brilla más o cambia el fondo (opcional)
            tint = colors.onPrimary,
            modifier = Modifier.size(26.dp)
        )
    }
}