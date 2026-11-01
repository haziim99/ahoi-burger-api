package com.menu.service

import com.menu.exception.BadRequestException
import com.menu.exception.NotFoundException
import com.menu.model.Category
import com.menu.model.MenuItem
import com.menu.repository.MenuItemRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import java.math.BigDecimal
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MenuServiceTest {

    private val repository = mock<MenuItemRepository>()
    private val mongo = mock<MongoTemplate>()
    private val service = MenuService(repository, mongo)

    private val bigBurger = MenuItem(
        id = "big-burger", name = "Big Burger", description = "Tasty", category = Category.BURGER,
        priceCents = 999, weightGrams = 180, specialIngredients = listOf("Bacon"), allergens = listOf("rr"),
    )

    @Test
    fun `get returns details with the price as a decimal`() {
        whenever(repository.findById("big-burger")).thenReturn(Optional.of(bigBurger))

        val result = service.get(Category.BURGER, "big-burger")

        assertEquals(BigDecimal("9.99"), result.price)
        assertEquals("USD", result.currency)
        assertEquals(listOf("Bacon"), result.specialIngredients)
    }

    @Test
    fun `get throws NotFoundException for an unknown id`() {
        whenever(repository.findById("nope")).thenReturn(Optional.empty())
        assertThrows<NotFoundException> { service.get(Category.BURGER, "nope") }
    }

    @Test
    fun `get throws NotFoundException when the item belongs to another category`() {
        whenever(repository.findById("big-burger")).thenReturn(Optional.of(bigBurger))
        assertThrows<NotFoundException> { service.get(Category.DRINK, "big-burger") }
    }

    @Test
    fun `list rejects minPrice greater than maxPrice and does not query the database`() {
        assertThrows<BadRequestException> {
            service.list(Category.BURGER, null, BigDecimal("10"), BigDecimal("5"), null)
        }
        verify(mongo, never()).find(any<Query>(), eq(MenuItem::class.java))
    }

    @Test
    fun `list rejects negative prices`() {
        assertThrows<BadRequestException> {
            service.list(Category.BURGER, null, BigDecimal("-1"), null, null)
        }
    }

    @Test
    fun `list rejects a search term that is too long`() {
        assertThrows<BadRequestException> {
            service.list(Category.BURGER, "x".repeat(101), null, null, null)
        }
    }

    @Test
    fun `list maps results and builds a query with all filters`() {
        whenever(mongo.find(any<Query>(), eq(MenuItem::class.java))).thenReturn(listOf(bigBurger))

        val result = service.list(Category.BURGER, "big", BigDecimal("5"), BigDecimal("10"), true)

        assertEquals(1, result.size)
        assertEquals("Big Burger", result[0].name)
        assertEquals(BigDecimal("9.99"), result[0].price)

        val captor = argumentCaptor<Query>()
        verify(mongo).find(captor.capture(), eq(MenuItem::class.java))
        val json = captor.firstValue.queryObject.toString()
        assertTrue(json.contains("category"))
        assertTrue(json.contains("priceCents"))
        assertTrue(json.contains("name"))
        assertTrue(json.contains("available"))
    }
}
