package com.example.safebite.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // UID del usuario actual
    private val userId get() = auth.currentUser?.uid

    // ── Referencia a las colecciones ──────────────────────────────────────────
    private fun favoritesRef() = userId?.let {
        db.collection("users").document(it).collection("favorites")
    }

    private fun historyRef() = userId?.let {
        db.collection("users").document(it).collection("scanHistory")
    }

    // ── Convertir Product a Map para Firestore ────────────────────────────────
    // ✅ Actualizado con los nuevos nombres de campos (product_name, image_front_url, etc)
    private fun Product.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "product_name" to product_name,
        "brands" to brands,
        "price" to price,
        "image_front_url" to image_front_url,
        "store" to store,
        "isFavorite" to isFavorite,
        "allergens_tags" to allergens_tags,
        "ingredients_text_es" to ingredients_text_es,
        "nutriscore_grade" to nutriscore_grade,
        "nova_group" to nova_group,
        "quantity" to quantity
    )

    // ── Convertir Map de Firestore a Product ──────────────────────────────────
    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any?>.toProduct() = Product(
        id = get("id") as? String ?: "", // ✅ Ahora es String
        product_name = get("product_name") as? String,
        brands = get("brands") as? String,
        price = (get("price") as? Double) ?: 0.0,
        image_front_url = get("image_front_url") as? String,
        store = get("store") as? String ?: "SafeBite Shop",
        isFavorite = get("isFavorite") as? Boolean ?: false,
        allergens_tags = get("allergens_tags") as? List<String>,
        ingredients_text_es = get("ingredients_text_es") as? String,
        nutriscore_grade = get("nutriscore_grade") as? String,
        nova_group = (get("nova_group") as? Long)?.toInt(),
        quantity = get("quantity") as? String
    )

    // ── FAVORITOS ─────────────────────────────────────────────────────────────

    suspend fun addFavorite(product: Product) {
        favoritesRef()
            ?.document(product.id) // ✅ product.id ya es String
            ?.set(product.toMap())
            ?.await()
    }

    suspend fun removeFavorite(productId: String) { // ✅ Cambiado de Int a String
        favoritesRef()
            ?.document(productId)
            ?.delete()
            ?.await()
    }

    suspend fun getFavorites(): List<Product> {
        return favoritesRef()
            ?.get()
            ?.await()
            ?.documents
            ?.mapNotNull { it.data?.toProduct() }
            ?: emptyList()
    }

    // ── HISTORIAL ─────────────────────────────────────────────────────────────

    suspend fun addToHistory(product: Product) {
        val uid = userId
        if (uid == null) return

        try {
            historyRef()
                ?.document(product.id) // ✅ product.id ya es String
                ?.set(product.toMap())
                ?.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getHistory(): List<Product> {
        return historyRef()
            ?.get()
            ?.await()
            ?.documents
            ?.mapNotNull { it.data?.toProduct() }
            ?: emptyList()
    }

    suspend fun clearHistory() {
        val docs = historyRef()?.get()?.await()?.documents ?: return
        docs.forEach { it.reference.delete().await() }
    }
}