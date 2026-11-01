package com.menu.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

enum class Category { BURGER, DRINK }

/**
 * One document type for burgers and drinks, told apart by [category].
 * Price is stored in cents (Int) to avoid floating point / BigDecimal-as-String issues in MongoDB.
 */
@Document("menu_items")
data class MenuItem(
    @Id val id: String? = null,
    val name: String,
    val description: String? = null,
    val category: Category,
    val priceCents: Int,
    val currency: String = "USD",
    val weightGrams: Int? = null,
    val volumeMl: Int? = null,
    val vegetarian: Boolean = false,
    val available: Boolean = true,
    val specialIngredients: List<String> = emptyList(),
    val allergens: List<String> = emptyList(),
    val imageUrl: String? = null,
)
