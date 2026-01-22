package com.example.safebite
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.example.safebite.controller.*
import com.example.safebite.model.AuthRepository
import com.example.safebite.view.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authRepo = AuthRepository()
        val authController = AuthController(authRepo)
        val productController = ProductController()

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = if (authRepo.isUserLoggedIn()) "home" else "login") {
                composable("login") { LoginScreen(navController, authController) }
                composable("register") { RegisterScreen(navController, authController) }
                composable("home") { HomeScreen(navController, authController) }
                composable("products") { ProductScreen(navController, productController) }
                composable("favorites") { FavoritesScreen(navController, productController) }
            }
        }
    }
}