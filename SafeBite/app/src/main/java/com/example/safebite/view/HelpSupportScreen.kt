package com.example.safebite.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.safebite.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ayuda y Soporte", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- CABECERA DE SOPORTE ---
            Text("¿Cómo podemos ayudarte?", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text("Busca en nuestras preguntas frecuentes o contacta con nosotros.", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // --- SECCIÓN DE CONTACTO RÁPIDO ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SupportContactCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Email,
                    title = "Email",
                    onClick = { /* Acción abrir correo */ }
                )
                SupportContactCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Chat,
                    title = "Chat",
                    onClick = { /* Acción abrir chat */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SECCIÓN FAQ ---
            Text("Preguntas Frecuentes", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)

            FaqItem(
                question = "¿Qué es SafeBite?",
                answer = "SafeBite es una aplicación diseñada para mejorar la seguridad alimentaria, permitiendo a los usuarios verificar productos y compartir reseñas sobre la calidad de los mismos."
            )
            FaqItem(
                question = "¿Cómo cambio mi contraseña?",
                answer = "Puedes cambiar tu contraseña desde la sección 'Editar Perfil' en tu menú de usuario."
            )
            FaqItem(
                question = "¿Es gratuita la aplicación?",
                answer = "Sí, SafeBite es completamente gratuita para todos los usuarios. Nuestra misión es mejorar la salud pública."
            )
            FaqItem(
                question = "¿Cómo reporto un problema?",
                answer = "Si encuentras un error o tienes un problema con un local, puedes escribirnos directamente a través del botón de Email arriba indicado."
            )

            // --- VERSIÓN DE LA APP ---
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("SafeBite v1.0.4", fontSize = 12.sp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun SupportContactCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = GreenPrimary, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    question,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp
                )
                Icon(
                    if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            AnimatedVisibility(visible = expanded) {
                Text(
                    text = answer,
                    modifier = Modifier.padding(top = 12.dp),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}