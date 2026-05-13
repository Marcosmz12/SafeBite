package com.example.safebite.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("_id") val id: String = "",
    val product_name: String? = null,
    val brands: String? = null,
    val image_front_url: String? = null,
    val allergens_tags: List<String>? = null,
    val labels_tags: List<String>? = null,
    val ingredients_text: String? = null,
    @SerializedName("ingredients_text_es") val ingredients_text_es: String? = null,
    val nutriscore_grade: String? = null,
    val ecoscore_grade: String? = null,
    val nova_group: Int? = null,
    val quantity: String? = null,
    val nutriments: Nutriments? = null,

    // Campos para tu lógica
    val isFavorite: Boolean = false,
    val price: Double = 0.0,
    val store: String = "SafeBite Shop"
) {
    // Helper para obtener el nombre o un placeholder
    val name: String get() = product_name ?: "Producto sin nombre"
    
    // Helper para la imagen
    val imageUrl: String? get() = image_front_url
}

data class Nutriments(
    @SerializedName("energy-kcal_100g") val energyKcal: Double? = null,
    @SerializedName("fat_100g") val fat: Double? = null,
    @SerializedName("sugars_100g") val sugars: Double? = null,
    @SerializedName("proteins_100g") val proteins: Double? = null,
    @SerializedName("salt_100g") val salt: Double? = null,
    @SerializedName("carbohydrates_100g") val carbohydrates: Double? = null,
    @SerializedName("saturated-fat_100g") val saturatedFat: Double? = null,
    @SerializedName("fiber_100g") val fiber: Double? = null
)

data class OpenFoodResponse(
    val status: Int,
    val product: Product?
)

data class OpenFoodSearchResponse(
    val count: Int = 0,
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 0,
    val products: List<Product> = emptyList()
)
