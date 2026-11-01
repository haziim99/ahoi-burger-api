package com.menu.controller

import com.menu.dto.RestaurantResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/restaurant")
@Tag(name = "Restaurant")
class RestaurantController(
    @Value("\${restaurant.name}") private val name: String,
    @Value("\${restaurant.address}") private val address: String,
) {

    @GetMapping
    @Operation(summary = "Restaurant name and address")
    fun restaurant() = RestaurantResponse(name, address)
}
