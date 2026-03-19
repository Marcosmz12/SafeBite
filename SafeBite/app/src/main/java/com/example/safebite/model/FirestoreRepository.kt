package com.example.safebite.model

import com.example.safebite.model.Product
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
    private fun Product.toMap(): Map<String, Any?> = mapOf(
        "id"                   to id,
        "name"                 to name,
        "store"                to store,
        "price"                to price,
        "imageUrl"             to imageUrl,
        "category"             to category,
        "isFavorite"           to isFavorite,
        "product_name"         to product_name,
        "brands"               to brands,
        "image_front_url"      to image_front_url,
        "allergens_tags"       to allergens_tags,
        "ingredients_text_es"  to ingredients_text_es
    )

    // ── Convertir Map de Firestore a Product ──────────────────────────────────
    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any?>.toProduct() = Product(
        id                  = (get("id") as? Long)?.toInt() ?: 0,
        name                = get("name") as? String ?: "",
        store               = get("store") as? String ?: "",
        price               = (get("price") as? Double) ?: 0.0,
        imageUrl            = get("imageUrl") as? String ?: "",
        category            = get("category") as? String ?: "",
        isFavorite          = get("isFavorite") as? Boolean ?: false,
        product_name        = get("product_name") as? String,
        brands              = get("brands") as? String,
        image_front_url     = get("image_front_url") as? String,
        allergens_tags      = get("allergens_tags") as? List<String>,
        ingredients_text_es = get("ingredients_text_es") as? String
    )

    // ── FAVORITOS ─────────────────────────────────────────────────────────────

    suspend fun addFavorite(product: Product) {
        favoritesRef()
            ?.document(product.id.toString())
            ?.set(product.toMap())
            ?.await()
    }

    suspend fun removeFavorite(productId: Int) {
        favoritesRef()
            ?.document(productId.toString())
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
        if (uid == null) {
            println("❌ ERROR: usuario no autenticado, uid es null")
            return
        }
        println("✅ Guardando en historial para uid: $uid")
        try {
            historyRef()
                ?.document(product.id.toString())
                ?.set(product.toMap())
                ?.await()
            println("✅ Guardado correctamente: ${product.name}")
        } catch (e: Exception) {
            println("❌ Error al guardar: ${e.message}")
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