package com.example.safebite.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    Scaffold(
        // --- BARRA SUPERIOR VERDE ---
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { /* Acción menú */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White // Blanco para resaltar sobre el verde
                        )
                    }
                },
                actions = {
                    Row(modifier = Modifier.padding(end = 10.dp)) {
                        SocialIcon(R.drawable.ic_facebook)
                        SocialIcon(R.drawable.ic_instagram)
                        SocialIcon(R.drawable.ic_x)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = greenColor // Fondo de la barra verde
                )
            )
        },
        // --- BARRA INFERIOR ESTILO PÍLDORA ---
        bottomBar = { SafeBiteBottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- CABECERA VERDE CON FOTOS ---
            Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                // Semicírculo verde
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                        .clip(RoundedCornerShape(bottomStart = 200.dp, bottomEnd = 200.dp))
                        .background(greenColor)
                )

                // Fotos circulares (Asegúrate de tener estos nombres en drawable)
                CircularPhoto(R.drawable.food1, Modifier.align(Alignment.TopStart).padding(start = 25.dp, top = 20.dp).size(85.dp))
                CircularPhoto(R.drawable.food2, Modifier.align(Alignment.TopCenter).padding(top = 5.dp).size(95.dp))
                CircularPhoto(R.drawable.food3, Modifier.align(Alignment.TopEnd).padding(end = 25.dp, top = 20.dp).size(85.dp))
                CircularPhoto(R.drawable.storefront, Modifier.align(Alignment.BottomCenter).size(160.dp))
            }

            Spacer(modifier = Modifier.height(25.dp))

            // --- TEXTOS ---
            Text(
                text = "Bienvenido",
                fontSize = 55.sp,
                fontWeight = FontWeight.Bold,
                color = greenColor
            )
            Text(
                text = "SAFEBITE",
                fontSize = 35.sp,
                fontWeight = FontWeight.Normal,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Encuentra alimentos sin alergias al mejor precio.",
                modifier = Modifier.padding(horizontal = 40.dp),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- BOTÓN CERRAR SESIÓN ---
            Button(
                onClick = {
                    controller.logout {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("CERRAR SESIÓN", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(100.dp)) // Espacio para que el menú no tape el contenido
        }
    }
}

// ==========================================
// COMPONENTES AUXILIARES
// ==========================================

@Composable
fun SafeBiteBottomBar(navController: NavHostController) {
    val greenColor = Color(0xFF55AA33)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(CircleShape)
                .background(greenColor),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomBottomIcon(Icons.Outlined.Home) {
                navController.navigate("home") { launchSingleTop = true }
            }
            CustomBottomIcon(Icons.Outlined.Restaurant) {
                navController.navigate("products") { launchSingleTop = true }
            }
            CustomBottomIcon(Icons.Outlined.FavoriteBorder) {
                navController.navigate("favorites") { launchSingleTop = true }
            }
            CustomBottomIcon(Icons.AutoMirrored.Outlined.Assignment) { }
            CustomBottomIcon(Icons.Outlined.SupportAgent) { }
        }
    }
}

@Composable
fun CustomBottomIcon(icon: ImageVector, onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
    }
}

@Composable
fun CircularPhoto(id: Int, modifier: Modifier) {
    Card(
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier.border(3.dp, Color.White, CircleShape)
    ) {
        Image(
            painter = painterResource(id),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SocialIcon(id: Int) {
    IconButton(onClick = { }) {
        Image(
            painter = painterResource(id),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
    }
}