package com.example.safebite.controller

import androidx.compose.runtime.mutableStateOf
import com.example.safebite.model.AuthRepository

class AuthController(private val repository: AuthRepository) {
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.value = "Campos vacíos"
            return
        }
        isLoading.value = true
        repository.signIn(email, password) { success, error ->
            isLoading.value = false
            if (success) onSuccess() else errorMessage.value = error
        }
    }

    fun register(email: String, password: String, confirm: String, onSuccess: () -> Unit) {
        if (password != confirm) {
            errorMessage.value = "Las contraseñas no coinciden"
            return
        }
        isLoading.value = true
        repository.signUp(email, password) { success, error ->
            isLoading.value = false
            if (success) onSuccess() else errorMessage.value = error
        }
    }

    fun logout(onLogout: () -> Unit) {
        repository.logout()
        onLogout()
    }
}