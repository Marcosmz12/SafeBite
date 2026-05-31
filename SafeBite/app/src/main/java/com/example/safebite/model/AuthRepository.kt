package com.example.safebite.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUserEmail(): String? {
        return com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email
    }
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logout() {
        auth.signOut()
    }

    fun signIn(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            onResult(it.isSuccessful, it.exception?.message)
        }
    }

    fun signUp(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance() // Asegúrate de tener esta línea

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 1. Obtenemos el ID real que Firebase le asignó al usuario
                    val userId = task.result?.user?.uid

                    if (userId != null) {
                        // 2. Creamos los datos iniciales para Firestore
                        val userMap = mapOf(
                            "username" to email.split("@")[0], // Nombre temporal basado en el email
                            "email" to email
                        )

                        // 3. Guardamos en la colección "users" con el ID del usuario
                        db.collection("users").document(userId)
                            .set(userMap)
                            .addOnCompleteListener { firestoreTask ->
                                if (firestoreTask.isSuccessful) {
                                    onResult(true, null)
                                } else {
                                    onResult(false, "Error al crear perfil en base de datos")
                                }
                            }
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun updateUserProfile(newName: String, newEmail: String, newPassword: String?, onResult: (Boolean, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser
        val userId = user?.uid ?: return

        val updates = mapOf("username" to newName, "email" to newEmail)

        // CAMBIAMOS .update(updates) POR .set(updates, SetOptions.merge())
        db.collection("users").document(userId)
            .set(updates, SetOptions.merge()) // <--- Esto crea el documento si no existe
            .addOnSuccessListener {
                // El resto del código se queda igual...
                user.updateEmail(newEmail).addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        if (!newPassword.isNullOrEmpty()) {
                            user.updatePassword(newPassword).addOnCompleteListener { passTask ->
                                if (passTask.isSuccessful) onResult(true, null)
                                else onResult(false, "Error Password: ${passTask.exception?.message}")
                            }
                        } else {
                            onResult(true, null)
                        }
                    } else {
                        onResult(false, "Error Email: ${emailTask.exception?.message}")
                    }
                }
            }
            .addOnFailureListener { onResult(false, "Error Firestore: ${it.message}") }
    }


    fun updateNotificationSettings(push: Boolean, email: Boolean, offers: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val settings = mapOf(
            "notifications" to mapOf(
                "push" to push,
                "email" to email,
                "offers" to offers
            )
        )
        db.collection("users").document(userId).set(settings, SetOptions.merge())
    }

    fun getNotificationSettings(onResult: (Map<String, Boolean>?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val notifications = document.get("notifications") as? Map<String, Boolean>
                onResult(notifications)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getUserProfile(onResult: (Map<String, Any>?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.data)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}