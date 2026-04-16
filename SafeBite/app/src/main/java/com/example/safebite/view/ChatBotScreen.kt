package com.example.safebite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Logout
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
import com.example.safebite.view.widgets.SafeBiteBottomBar
import com.example.safebite.view.widgets.SafeBiteTopBar
import kotlinx.coroutines.launch

// Modelo de mensaje
data class Message(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(
    navController: NavHostController,
    authController: com.example.safebite.controller.AuthController // 👈 Añadido
) {
    var userInput by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            Message("¡Hola! Soy el asistente de SafeBite. ¿En qué puedo ayudarte hoy?", false)
        )
    }

    // ── ESTADOS PARA EL MENÚ LATERAL ──────────────────────────────────────
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ── CONTENEDOR DEL MENÚ DESPLEGABLE (USANDO TU WIDGET) ─────────────────
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // USAMOS TU WIDGET PERSONALIZADO
            com.example.safebite.view.widgets.SafeBiteDrawerContent(
                navController = navController,
                authController = authController,
                drawerState = drawerState,
                scope = scope
            )
        }
    ) {
        // ── ESTRUCTURA PRINCIPAL (SCAFFOLD) ───────────────────────────────
        Scaffold(
            topBar = {
                SafeBiteTopBar(
                    title = "SafeBite",
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onProfileClick = {
                        navController.navigate("profile") // 👈 Esto llevará al usuario a la pantalla de perfil
                    }
                )
            },
            bottomBar = { SafeBiteBottomBar(navController) },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F5F5)) // Fondo gris muy claro para el chat
            ) {
                // Lista de mensajes
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = messages) { msg ->
                        ChatBubble(msg)
                    }
                }

                // Caja de entrada de texto
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userInput,
                            onValueChange = { userInput = it },
                            placeholder = { Text("Pregúntame sobre alérgenos...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(
                            onClick = {
                                if (userInput.isNotBlank()) {
                                    messages.add(Message(userInput, true))
                                    messages.add(
                                        Message(
                                            "Estoy analizando tu consulta... ¡Dime el nombre del producto!",
                                            false
                                        )
                                    )
                                    userInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(GreenPrimary, CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: Message) {
    val alignment = if (msg.isUser) Alignment.End else Alignment.Start
    val bgColor = if (msg.isUser) GreenPrimary else Color.White
    val textColor = if (msg.isUser) Color.White else Color.Black
    val shape = if (msg.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = bgColor,
            shape = shape,
            shadowElevation = 1.dp
        ) {
            Text(
                text = msg.text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = textColor,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }
    }
}