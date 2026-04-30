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

    fun getUserEmail(): String {
        return repository.getCurrentUserEmail() ?: ""
    }

    fun updateUserData(newName: String, newEmail: String, newPassword: String?, onResult: (Boolean) -> Unit) {
        if (newName.isEmpty() || newEmail.isEmpty()) {
            errorMessage.value = "Nombre y Email son obligatorios"
            onResult(false)
            return
        }

        isLoading.value = true
        repository.updateUserProfile(newName, newEmail, newPassword) { success, error ->
            isLoading.value = false
            if (success) {
                onResult(true)
            } else {
                errorMessage.value = error // Guardamos el error real
                onResult(false)
            }
        }
    }
    fun saveNotificationSettings(push: Boolean, email: Boolean, offers: Boolean) {
        repository.updateNotificationSettings(push, email, offers)
    }

}