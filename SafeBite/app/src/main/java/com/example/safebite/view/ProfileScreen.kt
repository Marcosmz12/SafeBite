package com.example.safebite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safebite.R
import com.example.safebite.controller.AuthController
import com.example.safebite.ui.theme.GreenPrimary
import com.example.safebite.view.widgets.SafeBiteBottomBar
import com.example.safebite.view.widgets.SafeBiteTopBar
import com.example.safebite.view.widgets.SafeBiteDrawerContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, authController: AuthController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SafeBiteDrawerContent(navController, authController, drawerState, scope)
        }
    ) {
        Scaffold(
            topBar = {
                SafeBiteTopBar(
                    title = stringResource(id = R.string.app_name),
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onProfileClick = {
                        navController.navigate("profile")
                    }
                )
            },
            bottomBar = { SafeBiteBottomBar(navController) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Perfil
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Brush.verticalGradient(listOf(GreenPrimary, Color.White))),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            Icon(
                                Icons.Outlined.Person,
                                null,
                                modifier = Modifier.padding(20.dp),
                                tint = GreenPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            stringResource(id = R.string.profile_default_name),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }

                // Lista de Opciones (CORREGIDO: Todas incluyen onClick)
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Opción Editar Datos (Ahora navega correctamente)
                    ProfileOptionItem(
                        icon = Icons.Outlined.Edit,
                        title = stringResource(id = R.string.profile_edit_data),
                        onClick = { navController.navigate("edit_profile") }
                    )

                    ProfileOptionItem(
                        icon = Icons.Outlined.NotificationsActive,
                        title = stringResource(id = R.string.profile_notifications),
                        onClick = { navController.navigate("notifications") }
                    )

                    ProfileOptionItem(
                        icon = Icons.Outlined.Shield,
                        title = stringResource(id = R.string.profile_privacy),
                        onClick = { navController.navigate("privacy") }
                    )

                    ProfileOptionItem(
                        icon = Icons.Outlined.HelpCenter,
                        title = stringResource(id = R.string.profile_help),
                        onClick = { navController.navigate("help") }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón de Cerrar Sesión
                    Button(
                        onClick = {
                            authController.logout {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFEBEE),
                            contentColor = Color.Red
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(id = R.string.menu_logout), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = GreenPrimary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Icon(Icons.Outlined.ChevronRight, null, tint = Color.Gray)
        }
    }
}