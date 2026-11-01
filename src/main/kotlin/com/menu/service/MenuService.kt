package com.menu.service

import com.menu.dto.ItemDetail
import com.menu.dto.ItemSummary
import com.menu.dto.toDetail
import com.menu.dto.toSummary
import com.menu.exception.BadRequestException
import com.menu.exception.NotFoundException
import com.menu.model.Category
import com.menu.model.MenuItem
import com.menu.repository.MenuItemRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.regex.Pattern

@Service
class MenuService(
    private val repository: MenuItemRepository,
    private val mongo: MongoTemplate,
) {

    fun list(
        category: Category,
        search: String?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?,
        available: Boolean?,
    ): List<ItemSummary> {
        validate(search, minPrice, maxPrice)

        val criteria = mutableListOf(Criteria.where("category").`is`(category))

        search?.trim()?.takeIf { it.isNotEmpty() }?.let {
            // Pattern.quote: user input is treated as plain text, never as a regex (no regex/NoSQL injection)
            criteria += Criteria.where("name").regex(Pattern.compile(Pattern.quote(it), Pattern.CASE_INSENSITIVE))
        }
        minPrice?.let { criteria += Criteria.where("priceCents").gte(toCents(it, RoundingMode.CEILING)) }
        maxPrice?.let { criteria += Criteria.where("priceCents").lte(toCents(it, RoundingMode.FLOOR)) }
        available?.let { criteria += Criteria.where("available").`is`(it) }

        val query = Query(Criteria().andOperator(criteria))
        return mongo.find(query, MenuItem::class.java).map { it.toSummary() }
    }

    fun get(category: Category, id: String): ItemDetail {
        val item = repository.findById(id).orElse(null)
        if (item == null || item.category != category) {
            throw NotFoundException("${category.name.lowercase()} '$id' not found")
        }
        return item.toDetail()
    }

    private fun validate(search: String?, minPrice: BigDecimal?, maxPrice: BigDecimal?) {
        if (search != null && search.trim().length > MAX_SEARCH_LENGTH) {
            throw BadRequestException("search must be at most $MAX_SEARCH_LENGTH characters")
        }
        listOf("minPrice" to minPrice, "maxPrice" to maxPrice).forEach { (name, value) ->
            if (value != null && (value.signum() < 0 || value > MAX_PRICE)) {
                throw BadRequestException("$name must be between 0 and $MAX_PRICE")
            }
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw BadRequestException("minPrice must not be greater than maxPrice")
        }
    }

    private fun toCents(price: BigDecimal, mode: RoundingMode): Int =
        price.movePointRight(2).setScale(0, mode).intValueExact()

    private companion object {
        const val MAX_SEARCH_LENGTH = 100
        val MAX_PRICE = BigDecimal("1000000")
    }
}
