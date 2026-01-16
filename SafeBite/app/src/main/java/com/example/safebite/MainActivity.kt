package com.example.safebite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safebite.controller.AuthController
import com.example.safebite.model.AuthRepository
import com.example.safebite.view.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos el Modelo y el Controlador
        val authRepository = AuthRepository()
        val authController = AuthController(authRepository)

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val currentUser = authRepository.getCurrentUser()

                NavHost(
                    navController = navController,
                    startDestination = if (currentUser != null) "home" else "login"
                ) {
                    composable("login") { LoginScreen(navController, authController) }
                    composable("register") { RegisterScreen(navController, authController) }
                    composable("home") { HomeScreen(navController, authController) }
                }
            }
        }
    }
}