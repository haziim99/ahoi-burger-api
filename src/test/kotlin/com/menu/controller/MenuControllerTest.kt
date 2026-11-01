package com.menu.controller

import com.menu.config.ApiKeyInterceptor
import com.menu.dto.ItemSummary
import com.menu.exception.GlobalExceptionHandler
import com.menu.exception.NotFoundException
import com.menu.model.Category
import com.menu.service.MenuService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal

class MenuControllerTest {

    private val service = mock<MenuService>()

    // Standalone setup: no Spring context / database needed.
    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(MenuController(service), RestaurantController("Ahoi Burger!", "Road 1, 4020 Linz, Austria"))
        .setControllerAdvice(GlobalExceptionHandler())
        .addInterceptors(ApiKeyInterceptor("test-key"))
        .build()

    private val summary = ItemSummary(
        id = "big-burger", name = "Big Burger", price = BigDecimal("9.99"), currency = "USD",
        weightGrams = 180, volumeMl = null, imageUrl = "/images/burger1.jpg", available = true,
    )

    @Test
    fun `GET burgers returns the list`() {
        whenever(service.list(eq(Category.BURGER), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(listOf(summary))

        mockMvc.get("/api/burgers") { header("X-API-Key", "test-key") }
            .andExpect {
                status { isOk() }
                jsonPath("\$[0].name") { value("Big Burger") }
                jsonPath("\$[0].price") { value(9.99) }
                jsonPath("\$[0].available") { value(true) }
            }
    }

    @Test
    fun `GET burgers passes search and price filters to the service`() {
        whenever(service.list(eq(Category.BURGER), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(emptyList())

        mockMvc.get("/api/burgers?search=big&minPrice=5&maxPrice=10&available=true") {
            header("X-API-Key", "test-key")
        }.andExpect { status { isOk() } }

        verify(service).list(Category.BURGER, "big", BigDecimal("5"), BigDecimal("10"), true)
    }

    @Test
    fun `request without API key is rejected with 401`() {
        mockMvc.get("/api/burgers").andExpect {
            status { isUnauthorized() }
            jsonPath("\$.status") { value(401) }
        }
    }

    @Test
    fun `request with a wrong API key is rejected with 401`() {
        mockMvc.get("/api/drinks") { header("X-API-Key", "wrong") }
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `unknown burger id returns 404 in the unified error format`() {
        whenever(service.get(Category.BURGER, "nope")).thenThrow(NotFoundException("burger 'nope' not found"))

        mockMvc.get("/api/burgers/nope") { header("X-API-Key", "test-key") }
            .andExpect {
                status { isNotFound() }
                jsonPath("\$.status") { value(404) }
                jsonPath("\$.message") { value("burger 'nope' not found") }
            }
    }

    @Test
    fun `invalid price parameter returns 400`() {
        mockMvc.get("/api/burgers?minPrice=abc") { header("X-API-Key", "test-key") }
            .andExpect { status { isBadRequest() } }
    }

    @Test
    fun `GET restaurant returns name and address`() {
        mockMvc.get("/api/restaurant") { header("X-API-Key", "test-key") }
            .andExpect {
                status { isOk() }
                jsonPath("\$.name") { value("Ahoi Burger!") }
                jsonPath("\$.address") { value("Road 1, 4020 Linz, Austria") }
            }
    }
}
