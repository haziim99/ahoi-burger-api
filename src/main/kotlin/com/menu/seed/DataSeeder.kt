package com.menu.seed

import com.menu.model.Category
import com.menu.model.MenuItem
import com.menu.repository.MenuItemRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

/** Inserts the demo data from the mockups on first start (only if the collection is empty). */
@Component
class DataSeeder(private val repository: MenuItemRepository) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(DataSeeder::class.java)

    override fun run(args: ApplicationArguments) {
        if (repository.count() > 0) return
        repository.saveAll(items)
        log.info("Seeded {} menu items", items.size)
    }

    private val items = listOf(
        MenuItem(
            id = "big-burger", name = "Big Burger", description = "This is a very tasty burger.",
            category = Category.BURGER, priceCents = 999, weightGrams = 180, vegetarian = false, available = true,
            specialIngredients = listOf("Bacon", "Tomatoes"), allergens = listOf("rr", "zz", "mm"),
            imageUrl = "/images/burger1.jpg",
        ),
        MenuItem(
            id = "small-burger", name = "Small Burger", description = "A smaller version of our classic.",
            category = Category.BURGER, priceCents = 599, weightGrams = 140, vegetarian = false, available = false,
            specialIngredients = listOf("Pickles"), allergens = listOf("rr"),
            imageUrl = "/images/burger2.jpg",
        ),
        MenuItem(
            id = "medium-burger", name = "Medium Burger", description = "The all-rounder.",
            category = Category.BURGER, priceCents = 799, weightGrams = 120, vegetarian = false, available = true,
            specialIngredients = listOf("Tomatoes"), allergens = listOf("zz", "mm"),
            imageUrl = "/images/burger3.jpg",
        ),
        MenuItem(
            id = "long-island-ice-tea", name = "Long Island Ice Tea", description = "Classic long drink.",
            category = Category.DRINK, priceCents = 650, volumeMl = 330, vegetarian = true, available = true,
            allergens = emptyList(), imageUrl = "/images/drink1.jpg",
        ),
        MenuItem(
            id = "orange-juice", name = "Orange Juice", description = "Freshly squeezed.",
            category = Category.DRINK, priceCents = 350, volumeMl = 250, vegetarian = true, available = true,
            allergens = emptyList(), imageUrl = "/images/drink2.jpg",
        ),
        MenuItem(
            id = "coffee", name = "Coffee", description = "Fresh roasted coffee.",
            category = Category.DRINK, priceCents = 280, volumeMl = 200, vegetarian = true, available = true,
            allergens = emptyList(), imageUrl = "/images/drink3.jpg",
        ),
    )
}
