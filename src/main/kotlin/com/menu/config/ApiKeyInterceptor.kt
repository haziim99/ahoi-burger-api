package com.menu.config

import com.menu.exception.UnauthorizedException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.security.MessageDigest

/** Rejects requests without a valid X-API-Key header. Failures become 401 via GlobalExceptionHandler. */
@Component
class ApiKeyInterceptor(
    @Value("\${app.api-key}") private val apiKey: String,
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (request.method == "OPTIONS") return true
        val provided = request.getHeader(HEADER)
        // constant-time comparison
        val valid = provided != null && MessageDigest.isEqual(provided.toByteArray(), apiKey.toByteArray())
        if (!valid) throw UnauthorizedException("Missing or invalid API key")
        return true
    }

    companion object {
        const val HEADER = "X-API-Key"
    }
}
