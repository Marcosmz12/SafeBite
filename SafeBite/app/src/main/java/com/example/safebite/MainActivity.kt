package com.example.safebite

// Imports de Compose básicos y Layout

// Imports de Material3 (Vital para Surface y Temas)

// Imports de Navegación

// Imports de TUS controladores y lógica (Ajusta si tus paquetes son diferentes)

// Imports de TUS pantallas
// 1. Imports de Android y ciclo de vida

// 2. Imports de Layout (Para Box, Column, Alignment, Arrangement, etc.)

// 3. Imports de Material 3 (Para Surface, Text, Button, CircularProgressIndicator, etc.)

// 4. Imports de Compose Runtime (Para LaunchedEffect, remember, etc.)

// 5. Imports de Navegación

// 6. Imports de TUS archivos (Asegúrate de que estas rutas coincidan con tus carpetas)
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.safebite.controller.AuthController
import com.example.safebite.controller.ProductController
import com.example.safebite.model.AuthRepository
import com.example.safebite.ui.theme.SafeBiteTheme
import com.example.safebite.view.ChatBotScreen
import com.example.safebite.view.FavoritesScreen
import com.example.safebite.view.HomeScreen
import com.example.safebite.view.LoginScreen
import com.example.safebite.view.ProductDetailScreen
import com.example.safebite.view.ProductScreen
import com.example.safebite.view.RegisterScreen
import com.example.safebite.view.ScannerScreen
import com.example.safebite.view.StartScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authRepo = AuthRepository()
        val authController = AuthController(authRepo)
        val productController = ProductController()

        setContent {
            SafeBiteTheme { // Quité el () para que use el darkTheme automático del sistema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // IMPORTANTE: usa el color del tema
                ) {
                    val navController = rememberNavController()
                    val destination = if (authRepo.isUserLoggedIn()) "home" else "start"

                    NavHost(navController = navController, startDestination = destination) {
                        composable("start") { StartScreen(navController) }
                        composable("login") { LoginScreen(navController, authController) }
                        composable("register") { RegisterScreen(navController, authController) }
                        composable("home") { HomeScreen(navController, authController) }
                        composable("products") { ProductScreen(navController, productController) }
                        composable("chatbot") {
                            ChatBotScreen(navController)
                        }
                        composable("favorites") {
                            FavoritesScreen(
                                navController,
                                productController
                            )
                        }
                        // Dentro de tu NavHost en MainActivity.kt
                        composable("productDetail/{barcode}") { backStackEntry ->
                            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
                            val colors = MaterialTheme.colorScheme

                            LaunchedEffect(barcode) {
                                productController.fetchProduct(barcode)  // ya no necesita suspend, funciona igual
                            }

                            // Usa 'by' para que Compose recomponga correctamente
                            val product by productController.scannedProduct
                            val isLoading by productController.isLoading
                            val errorMessage by productController.error

                            Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
                                when {
                                    isLoading -> {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = colors.primary)
                                        }
                                    }
                                    errorMessage != null -> {
                                        Column(
                                            modifier = Modifier.fillMaxSize().padding(20.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = errorMessage!!, color = colors.error, textAlign = TextAlign.Center)
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Button(onClick = { navController.popBackStack() }) {
                                                Text("Volver")
                                            }
                                        }
                                    }
                                    product != null -> {
                                        ProductDetailScreen(product!!) // Ahora el tipo es Product, no Any
                                    }
                                }
                            }
                        }

                            composable("scanner") { ScannerScreen(navController) }
                    }
                }
            }
        }
    }
}