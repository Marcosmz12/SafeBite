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
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    Row(modifier = Modifier.padding(end = 8.dp)) {
                        // Si te da error aquí, asegúrate de tener estos archivos en res/drawable
                        SocialIcon(R.drawable.ic_facebook)
                        SocialIcon(R.drawable.ic_instagram)
                        SocialIcon(R.drawable.ic_x)
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 25.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .clip(CircleShape)
                        .background(greenColor),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically // CORREGIDO AQUÍ
                ) {
                    CustomBottomIcon(Icons.Outlined.Home)
                    CustomBottomIcon(Icons.Outlined.Restaurant)
                    CustomBottomIcon(Icons.Outlined.FavoriteBorder)
                    CustomBottomIcon(Icons.AutoMirrored.Outlined.Assignment)
                    CustomBottomIcon(Icons.Outlined.SupportAgent)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .clip(RoundedCornerShape(bottomStart = 200.dp, bottomEnd = 200.dp))
                        .background(greenColor)
                )

                CircularPhoto(R.drawable.food1, Modifier.align(Alignment.TopStart).padding(start = 25.dp, top = 40.dp).size(75.dp))
                CircularPhoto(R.drawable.food2, Modifier.align(Alignment.TopCenter).padding(top = 10.dp).size(85.dp))
                CircularPhoto(R.drawable.food3, Modifier.align(Alignment.TopEnd).padding(end = 25.dp, top = 40.dp).size(75.dp))
                CircularPhoto(R.drawable.storefront, Modifier.align(Alignment.BottomCenter).size(150.dp))
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text("Bienvenido", fontSize = 55.sp, fontWeight = FontWeight.Bold, color = greenColor)
            Text("SAFEBITE", fontSize = 35.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Te damos la bienvenida a Safebite donde podrás encontrar productos y alimentos en diferentes supermercados si tienes alergias",
                fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    controller.logout {
                        navController.navigate("login") { popUpTo("home") { inclusive = true } }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.6f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("CERRAR SESIÓN", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CustomBottomIcon(icon: ImageVector) {
    IconButton(onClick = { }) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
    }
}

@Composable
fun CircularPhoto(resId: Int, modifier: Modifier) {
    Card(shape = CircleShape, elevation = CardDefaults.cardElevation(6.dp), modifier = modifier) {
        Image(painterResource(id = resId), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun SocialIcon(resId: Int) {
    IconButton(onClick = { }) {
        Icon(painterResource(id = resId), null, modifier = Modifier.size(24.dp), tint = Color.Unspecified)
    }
}