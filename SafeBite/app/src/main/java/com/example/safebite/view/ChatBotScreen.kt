package com.example.safebite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // EL IMPORT POSTA
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.* // USAMOS TODO EL 3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// Si ya definiste Message en otro lado, borrá estas líneas:
data class Message(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(navController: NavHostController) {
    val greenColor = Color(0xFF55AA33)
    var userInput by remember { mutableStateOf<String>("") }

    val messages = remember {
        mutableStateListOf(
            Message("¡Hola! Soy el asistente de SafeBite. ¿Qué receta buscás hoy?", false)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asistente de Recetas", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                // Si acá te da "Ambiguity", es porque tenés imports de Material 2 mezclados.
                colors = TopAppBarDefaults.topAppBarColors(containerColor = greenColor)
            )
        },
        bottomBar = { SafeBiteBottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Usamos el nombre del parámetro 'items' para evitar la ambigüedad
                items(items = messages) { msg ->
                    ChatBubble(msg)
                }
            }

            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = Color.White) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        placeholder = { Text("Preguntame una receta...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (userInput.isNotBlank()) {
                                messages.add(Message(userInput, true))
                                messages.add(Message("¡Dale! Te busco esa receta...", false))
                                userInput = ""
                            }
                        },
                        modifier = Modifier.background(greenColor, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: Message) {
    val alignment = if (msg.isUser) Alignment.End else Alignment.Start
    val bgColor = if (msg.isUser) Color(0xFF55AA33) else Color.White

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalAlignment = alignment) {
        Surface(color = bgColor, shape = RoundedCornerShape(12.dp), shadowElevation = 2.dp) {
            Text(text = msg.text, modifier = Modifier.padding(12.dp), color = if (msg.isUser) Color.White else Color.Black, fontSize = 15.sp)
        }
    }
}