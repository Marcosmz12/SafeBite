package com.example.safebite.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.safebite.controller.AuthController

@Composable
fun HomeScreen(navController: NavHostController, controller: AuthController) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("¡Bienvenido a SafeBite!")
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            controller.logout {
                navController.navigate("login") { popUpTo("home") { inclusive = true } }
            }
        }) { Text("Cerrar Sesión") }
    }
}