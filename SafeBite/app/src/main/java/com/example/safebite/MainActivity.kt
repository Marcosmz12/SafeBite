package com.example.safebite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// Imports de Compose básicos y Layout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

// Imports de Material3 (Vital para Surface y Temas)
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

// Imports de Navegación
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Imports de TUS controladores y lógica (Ajusta si tus paquetes son diferentes)
import com.example.safebite.controller.AuthController
import com.example.safebite.controller.ProductController
import com.example.safebite.model.AuthRepository
import com.example.safebite.ui.theme.SafeBiteTheme
import com.example.safebite.view.ChatBotScreen

// Imports de TUS pantallas
import com.example.safebite.view.StartScreen
import com.example.safebite.view.LoginScreen
import com.example.safebite.view.RegisterScreen
import com.example.safebite.view.HomeScreen
import com.example.safebite.view.ProductScreen
import com.example.safebite.view.FavoritesScreen

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
                    }
                }
            }
        }
    }
}