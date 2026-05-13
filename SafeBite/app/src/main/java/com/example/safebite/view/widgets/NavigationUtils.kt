package com.example.safebite.view.widgets

import androidx.navigation.NavHostController

/**
 * Función de extensión para navegar limpiando la pila.
 * Definida aquí para que sea accesible desde toda la aplicación.
 */
fun NavHostController.navigateAndClean(route: String) {
    this.navigate(route) {
        popUpTo(this@navigateAndClean.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}