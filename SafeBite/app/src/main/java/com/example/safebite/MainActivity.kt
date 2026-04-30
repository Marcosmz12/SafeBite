package com.example.safebite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.runtime.getValue
import com.example.safebite.view.EditProfileScreen
import com.example.safebite.view.NotificationsScreen
import com.example.safebite.view.ProfileScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authRepo = AuthRepository()
        val authController = AuthController(authRepo)
        val productController = ProductController()

        setContent {
            SafeBiteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val destination = if (authRepo.isUserLoggedIn()) "home" else "start"

                    NavHost(navController = navController, startDestination = destination) {
                        composable("start") { StartScreen(navController) }
                        composable("login") { LoginScreen(navController, authController) }
                        composable("register") { RegisterScreen(navController, authController) }
                        composable("home") {
                            HomeScreen(
                                navController,
                                authController,
                                productController
                            )
                        }
                        composable("products") {
                            ProductScreen(navController, productController, authController)
                        }
                        composable("profile") {
                            ProfileScreen(navController, authController)
                        }

                        composable("chatbot") {
                            ChatBotScreen(
                                navController,
                                authController
                            )
                        }
                        composable("favorites") {
                            FavoritesScreen(
                                navController,
                                productController,
                                authController,
                            )
                        }
                        composable("scanner") { ScannerScreen(navController) }

                        composable("product_detail_general") {
                            val selectedProduct by productController.selectedProduct

                            if (selectedProduct != null) {
                                ProductDetailScreen(
                                    navController = navController,
                                    product = selectedProduct!!
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.popBackStack()
                                }
                            }
                        }

                        composable("productDetail/{barcode}") { backStackEntry ->
                            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
                            val colors = MaterialTheme.colorScheme

                            LaunchedEffect(barcode) {
                                productController.fetchProduct(barcode)
                            }

                            val product by productController.scannedProduct
                            val isLoading by productController.isLoading
                            val errorMessage by productController.error

                            Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
                                when {
                                    isLoading -> {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(color = colors.primary)
                                        }
                                    }

                                    errorMessage != null -> {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(20.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = errorMessage!!,
                                                color = colors.error,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Button(onClick = { navController.popBackStack() }) {
                                                Text(
                                                    "Volver"
                                                )
                                            }
                                        }
                                    }

                                    product != null -> {
                                        ProductDetailScreen(
                                            navController = navController,
                                            product = product!!
                                        )
                                    }
                                }
                            }
                        }
                        composable("profile") { ProfileScreen(navController, authController) }
                        composable("edit_profile") { EditProfileScreen(navController, authController) }
                        composable("notifications") { NotificationsScreen(navController, authController) }
                    }
                }
            }
        }
    }
}