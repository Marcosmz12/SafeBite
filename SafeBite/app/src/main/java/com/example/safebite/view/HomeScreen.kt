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
        topBar = {
            TopAppBar(title = {}, actions = {
                Row(Modifier.padding(end = 10.dp)) {
                    SocialIcon(R.drawable.ic_facebook)
                    SocialIcon(R.drawable.ic_instagram)
                    SocialIcon(R.drawable.ic_x)
                }
            })
        },
        bottomBar = { SafeBiteBottomBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.fillMaxWidth().height(280.dp)) {
                Box(Modifier.fillMaxWidth().fillMaxHeight(0.8f).clip(RoundedCornerShape(bottomStart = 200.dp, bottomEnd = 200.dp)).background(greenColor))
                CircularPhoto(R.drawable.food1, Modifier.align(Alignment.TopStart).padding(30.dp).size(70.dp))
                CircularPhoto(R.drawable.food2, Modifier.align(Alignment.TopCenter).padding(10.dp).size(80.dp))
                CircularPhoto(R.drawable.food3, Modifier.align(Alignment.TopEnd).padding(30.dp).size(70.dp))
                CircularPhoto(R.drawable.storefront, Modifier.align(Alignment.BottomCenter).size(140.dp))
            }
            Text("Bienvenido", fontSize = 50.sp, fontWeight = FontWeight.Bold, color = greenColor)
            Text("SAFEBITE", fontSize = 35.sp, color = Color.DarkGray)
            Text("Encuentra alimentos sin alergias al mejor precio.", Modifier.padding(20.dp), textAlign = TextAlign.Center)
            Button(onClick = { controller.logout { navController.navigate("login") { popUpTo("home") { inclusive = true } } } }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("CERRAR SESIÓN")
            }
        }
    }
}

@Composable
fun SafeBiteBottomBar(navController: NavHostController) {
    val greenColor = Color(0xFF55AA33)
    Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)) {
        Row(Modifier.fillMaxWidth().height(70.dp).clip(CircleShape).background(greenColor), Arrangement.SpaceEvenly, Alignment.CenterVertically) {
            CustomBottomIcon(Icons.Outlined.Home) { navController.navigate("home") }
            CustomBottomIcon(Icons.Outlined.Restaurant) { navController.navigate("products") }
            CustomBottomIcon(Icons.Outlined.FavoriteBorder) { navController.navigate("favorites") }
            CustomBottomIcon(Icons.AutoMirrored.Outlined.Assignment) {}
            CustomBottomIcon(Icons.Outlined.SupportAgent) {}
        }
    }
}

@Composable
fun CustomBottomIcon(icon: ImageVector, onClick: () -> Unit) {
    IconButton(onClick = onClick) { Icon(icon, null, tint = Color.White, modifier = Modifier.size(30.dp)) }
}

@Composable
fun CircularPhoto(id: Int, modifier: Modifier) {
    Card(shape = CircleShape, elevation = CardDefaults.cardElevation(4.dp), modifier = modifier) {
        Image(painterResource(id), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun SocialIcon(id: Int) {
    Icon(painterResource(id), null, Modifier.size(24.dp).padding(horizontal = 4.dp))
}