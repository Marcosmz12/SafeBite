package com.example.safebite.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.safebite.controller.AuthController
import com.example.safebite.ui.theme.GreenPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController, authController: AuthController) {
    // IMPORTANTE: Asegúrate de que estos "by" no marquen error en rojo
    var name by remember { mutableStateOf("") } // El nombre suele venir de Firestore, por ahora vacío
    var email by remember { mutableStateOf(authController.getUserEmail()) }
    var password by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Editar Datos") },
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { nuevoNombre -> name = nuevoNombre }, // Cambiado 'it' por 'nuevoNombre'
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.Person, null) }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { nuevoEmail -> email = nuevoEmail }, // Cambiado 'it' por 'nuevoEmail'
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.Email, null) }
            )

            OutlinedTextField(
                value = password,
                onValueChange = { nuevaPass -> password = nuevaPass }, // Cambiado 'it' por 'nuevaPass'
                label = { Text("Nueva contraseña (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Outlined.Lock, null) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    authController.updateUserData(name, email, password) { success ->
                        scope.launch {
                            if (success) {
                                snackbarHostState.showSnackbar("Datos actualizados")
                                navController.popBackStack()
                            } else {
                                // MOSTRAMOS EL ERROR REAL QUE VIENE DEL CONTROLADOR
                                val errorMsg = authController.errorMessage.value ?: "Error desconocido"
                                snackbarHostState.showSnackbar(errorMsg)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("Guardar Cambios", fontWeight = FontWeight.Bold)
            }
        }
    }
}