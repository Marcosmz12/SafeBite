package com.example.safebite.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

private val GreenPrimary = Color(0xFF2E7D32)

@Composable
fun SafeBiteBottomBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Surface(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .fillMaxWidth(),
        color = GreenPrimary,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                Icons.Outlined.Home,
                "home",
                currentRoute
            ) { navController.navigate("home") }
            BottomNavItem(
                Icons.Outlined.Restaurant,
                "products",
                currentRoute
            ) { navController.navigate("products") }
            BottomNavItem(
                Icons.Outlined.FavoriteBorder,
                "favorites",
                currentRoute
            ) { navController.navigate("favorites") }
            BottomNavItem(
                Icons.Outlined.SupportAgent,
                "chatbot",
                currentRoute
            ) { navController.navigate("chatbot") }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    route: String,
    currentRoute: String?,
    onClick: () -> Unit
) {
    val isActive = currentRoute == route

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) Color.White.copy(alpha = 0.2f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = route,
            tint = if (isActive) Color.White else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
    }
}
