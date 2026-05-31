package com.example.safebite

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.safebite.controller.AuthController
import com.example.safebite.controller.ProductController
import com.example.safebite.model.AuthRepository
import com.example.safebite.ui.theme.SafeBiteTheme
import com.example.safebite.view.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Repositorio y controlador de Auth (se pueden mantener aquí o pasar a ViewModel)
        val authRepo = AuthRepository()
        val authController = AuthController(authRepo)

        setContent {
            SafeBiteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val destination = if (authRepo.isUserLoggedIn()) "home" else "start"
                    val productController: ProductController = viewModel()

                    NavHost(navController = navController, startDestination = destination) {
                        // 1. Autenticación y Bienvenida
                        composable("start") { StartScreen(navController) }
                        composable("login") { LoginScreen(navController, authController) }
                        composable("register") { RegisterScreen(navController, authController) }

                        // 2. Pantallas Principales
                        composable("home") {
                            HomeScreen(navController, authController, productController)
                        }
                        composable("products") {
                            ProductScreen(navController, productController, authController)
                        }
                        composable("favorites") {
                            FavoritesScreen(navController, productController, authController)
                        }

                        // 3. Escáner y Detalles (Círculo cerrado)
                        composable("scanner") {
                            ScannerScreen(navController, productController)
                        }
                        composable("productDetail") {
                            ProductDetailScreen(navController, productController)
                        }

                        // 4. Perfil y Configuración
                        composable("profile") {
                            ProfileScreen(navController, authController)
                        }
                        composable("edit_profile") {
                            EditProfileScreen(navController, authController)
                        }
                        composable("notifications") {
                            NotificationsScreen(navController, authController)
                        }

                        // 5. Otros
                        composable("chatbot") {
                            ChatBotScreen(navController, authController)
                        }

                        composable("privacy") { PrivacyScreen(navController, authController) }
                        composable("help") { HelpSupportScreen(navController) }
                    }
                }
            }
        }
    }
}

