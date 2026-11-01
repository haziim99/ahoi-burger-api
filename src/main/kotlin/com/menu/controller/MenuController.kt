package com.menu.controller

import com.menu.dto.ItemDetail
import com.menu.dto.ItemSummary
import com.menu.model.Category
import com.menu.service.MenuService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api")
@Tag(name = "Menu", description = "Burgers and drinks")
class MenuController(private val service: MenuService) {

    @GetMapping("/burgers")
    @Operation(summary = "List burgers", description = "Unavailable burgers are included and flagged with available=false.")
    fun burgers(
        @Parameter(description = "Case-insensitive name search") @RequestParam(required = false) search: String?,
        @Parameter(description = "Minimum price (e.g. 5.00)") @RequestParam(required = false) minPrice: BigDecimal?,
        @Parameter(description = "Maximum price (e.g. 10.00)") @RequestParam(required = false) maxPrice: BigDecimal?,
        @Parameter(description = "Filter by availability") @RequestParam(required = false) available: Boolean?,
    ): List<ItemSummary> = service.list(Category.BURGER, search, minPrice, maxPrice, available)

    @GetMapping("/burgers/{id}")
    @Operation(summary = "Burger details")
    fun burger(@PathVariable id: String): ItemDetail = service.get(Category.BURGER, id)

    @GetMapping("/drinks")
    @Operation(summary = "List drinks")
    fun drinks(
        @Parameter(description = "Case-insensitive name search") @RequestParam(required = false) search: String?,
        @Parameter(description = "Minimum price (e.g. 2.00)") @RequestParam(required = false) minPrice: BigDecimal?,
        @Parameter(description = "Maximum price (e.g. 6.00)") @RequestParam(required = false) maxPrice: BigDecimal?,
        @Parameter(description = "Filter by availability") @RequestParam(required = false) available: Boolean?,
    ): List<ItemSummary> = service.list(Category.DRINK, search, minPrice, maxPrice, available)

    @GetMapping("/drinks/{id}")
    @Operation(summary = "Drink details")
    fun drink(@PathVariable id: String): ItemDetail = service.get(Category.DRINK, id)
}
