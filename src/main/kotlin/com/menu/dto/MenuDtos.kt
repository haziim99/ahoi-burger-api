package com.menu.dto

import com.menu.model.MenuItem
import java.math.BigDecimal

data class RestaurantResponse(
    val name: String,
    val address: String,
)

/** Used in list responses (overview screen). */
data class ItemSummary(
    val id: String,
    val name: String,
    val price: BigDecimal,
    val currency: String,
    val weightGrams: Int?,
    val volumeMl: Int?,
    val imageUrl: String?,
    val available: Boolean,
)

/** Used for the detail screen. */
data class ItemDetail(
    val id: String,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val currency: String,
    val weightGrams: Int?,
    val volumeMl: Int?,
    val vegetarian: Boolean,
    val available: Boolean,
    val specialIngredients: List<String>,
    val allergens: List<String>,
    val imageUrl: String?,
)

private fun centsToPrice(cents: Int): BigDecimal = BigDecimal(cents).movePointLeft(2)

fun MenuItem.toSummary() = ItemSummary(
    id = id!!,
    name = name,
    price = centsToPrice(priceCents),
    currency = currency,
    weightGrams = weightGrams,
    volumeMl = volumeMl,
    imageUrl = imageUrl,
    available = available,
)

fun MenuItem.toDetail() = ItemDetail(
    id = id!!,
    name = name,
    description = description,
    price = centsToPrice(priceCents),
    currency = currency,
    weightGrams = weightGrams,
    volumeMl = volumeMl,
    vegetarian = vegetarian,
    available = available,
    specialIngredients = specialIngredients,
    allergens = allergens,
    imageUrl = imageUrl,
)
