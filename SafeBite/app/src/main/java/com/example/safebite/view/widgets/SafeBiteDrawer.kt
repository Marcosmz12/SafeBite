package com.example.safebite.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safebite.R
import com.example.safebite.controller.AuthController
import com.example.safebite.ui.theme.GreenPrimary
import kotlinx.coroutines.launch

@Composable
fun SafeBiteDrawerContent(
    navController: NavHostController,
    authController: AuthController,
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    ModalDrawerSheet(drawerContainerColor = Color.White) {
        // Cabecera con el icono de cuenta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(24.dp)
        ) {
            Column {
                Icon(
                    Icons.Outlined.AccountCircle,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
                Text(
                    stringResource(id = R.string.drawer_app_name),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    stringResource(id = R.string.drawer_welcome),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 1. INICIO
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Home, null) },
            label = { Text(stringResource(id = R.string.menu_home)) },
            selected = false,
            onClick = { scope.launch { drawerState.close() }; navController.navigate("home") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // 2. PRODUCTOS (Explorar)
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Restaurant, null) },
            label = { Text(stringResource(id = R.string.menu_products)) },
            selected = false,
            onClick = { scope.launch { drawerState.close() }; navController.navigate("products") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // 3. FAVORITOS
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.FavoriteBorder, null) },
            label = { Text(stringResource(id = R.string.title_favorites)) },
            selected = false,
            onClick = { scope.launch { drawerState.close() }; navController.navigate("favorites") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // 4. CHATBOT
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.SupportAgent, null) },
            label = { Text(stringResource(id = R.string.title_chatbot)) },
            selected = false,
            onClick = { scope.launch { drawerState.close() }; navController.navigate("chatbot") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // 5. MI PERFIL
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Person, null) },
            label = { Text(stringResource(id = R.string.menu_profile)) },
            selected = false,
            onClick = { scope.launch { drawerState.close() }; navController.navigate("profile") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp))

        // ── SELECTOR DE IDIOMA ──────────────────────────────────────────────
        LanguageSelector()

        Spacer(modifier = Modifier.weight(1f))

        // 6. CERRAR SESIÓN
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Logout, null, tint = Color.Red) },
            label = { Text(stringResource(id = R.string.menu_logout), color = Color.Red) },
            selected = false,
            onClick = {
                scope.launch { drawerState.close() }
                authController.logout {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
    }
}