package com.example.safebite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun PrivacyScreen(navController: NavHostController, authController: AuthController) {
    var shareData by remember { mutableStateOf(true) }
    var profilePublic by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Privacidad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver", tint = GreenPrimary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- SECCIÓN: PREFERENCIAS DE DATOS ---
            Text("Configuración de datos", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PrivacyToggleItem(
                        title = "Compartir analíticas",
                        description = "Ayúdanos a mejorar SafeBite enviando datos de uso anónimos.",
                        icon = Icons.Outlined.BarChart,
                        checked = shareData,
                        onCheckedChange = { shareData = it }
                    )
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                    PrivacyToggleItem(
                        title = "Perfil público",
                        description = "Permitir que otros usuarios vean tu nombre en las reseñas.",
                        icon = Icons.Outlined.Visibility,
                        checked = profilePublic,
                        onCheckedChange = { profilePublic = it }
                    )
                }
            }

            // --- SECCIÓN: SEGURIDAD ---
            Text("Seguridad", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Lock, null, tint = GreenPrimary)
                        Spacer(Modifier.width(12.dp))
                        Text("Historial de inicios de sesión", modifier = Modifier.weight(1f))
                        Icon(Icons.Outlined.ChevronRight, null, tint = Color.Gray)
                    }
                }
            }

            // --- SECCIÓN: ZONA DE PELIGRO ---
            Spacer(modifier = Modifier.height(20.dp))
            Text("Zona de Peligro", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 14.sp)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Eliminar mi cuenta",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                    Text(
                        "Esta acción es permanente y borrará todos tus datos de SafeBite de forma inmediata.",
                        fontSize = 12.sp,
                        color = Color(0xFFB71C1C),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Button(
                        onClick = { /* Acción para borrar cuenta */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar para siempre", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacyToggleItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = GreenPrimary)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(description, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimary)
        )
    }
}