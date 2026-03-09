package com.example.safebite.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safebite.R

@Composable
fun StartScreen(navController: NavHostController) {
    // Obtenemos la paleta de colores del sistema (Material 3)
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxSize(),
        // El fondo será blanco en modo claro y casi negro en modo oscuro
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- LOGO ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    // Usamos el color primario del tema (Verde)
                    .background(colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_safebite),
                    contentDescription = null,
                    // onPrimary asegura que el logo se vea bien sobre el fondo verde
                    colorFilter = ColorFilter.tint(colors.onPrimary)
                )
            }

            Text(
                "SafeBite",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.primary // Ahora será el verde definido arriba
            )

            Spacer(modifier = Modifier.height(50.dp))

            // --- BOTÓN INICIAR SESIÓN ---
            // BOTÓN INICIAR SESIÓN
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = Color.White // Forzamos blanco para que no salga morado
                )
            ) {
                Text("INICIAR SESIÓN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(15.dp))

            // BOTÓN REGISTRARSE
            OutlinedButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                border = BorderStroke(2.dp, colors.primary), // Borde verde más grueso
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.primary // Texto verde
                )
            ) {
                Text("REGISTRARSE", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}