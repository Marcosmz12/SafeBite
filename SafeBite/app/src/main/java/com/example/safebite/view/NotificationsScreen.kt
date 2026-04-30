package com.example.safebite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safebite.controller.AuthController
import com.example.safebite.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavHostController, authController: AuthController) {
    // Estados para los interruptores
    var pushEnabled by remember { mutableStateOf(true) }
    var emailEnabled by remember { mutableStateOf(false) }
    var offersEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Configura cómo quieres recibir tus alertas de SafeBite.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            NotificationSettingItem(
                title = "Notificaciones Push",
                description = "Recibir alertas directas en el móvil",
                icon = Icons.Outlined.NotificationsActive,
                checked = pushEnabled,
                onCheckedChange = { pushEnabled = it }
            )

            NotificationSettingItem(
                title = "Alertas por Email",
                description = "Resumen semanal de seguridad alimentaria",
                icon = Icons.Outlined.Email,
                checked = emailEnabled,
                onCheckedChange = { emailEnabled = it }
            )

            NotificationSettingItem(
                title = "Ofertas y Novedades",
                description = "Nuevas funcionalidades y descuentos",
                icon = Icons.Outlined.LocalOffer,
                checked = offersEnabled,
                onCheckedChange = { offersEnabled = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // LLAMADA AL CONTROLADOR PARA GUARDAR
                    authController.saveNotificationSettings(pushEnabled, emailEnabled, offersEnabled)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("Guardar Preferencias", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun NotificationSettingItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = GreenPrimary, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(description, fontSize = 12.sp, color = Color.Gray)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimary)
        )
    }
}