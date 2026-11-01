package com.menu.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.Instant

data class ApiError(
    val status: Int,
    val error: String,
    val message: String,
    val details: Map<String, String> = emptyMap(),
    val timestamp: Instant = Instant.now(),
)

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(NotFoundException::class)
    fun notFound(e: NotFoundException) = build(HttpStatus.NOT_FOUND, e.message ?: "Not found")

    @ExceptionHandler(BadRequestException::class)
    fun badRequest(e: BadRequestException) = build(HttpStatus.BAD_REQUEST, e.message ?: "Bad request")

    @ExceptionHandler(UnauthorizedException::class)
    fun unauthorized(e: UnauthorizedException) = build(HttpStatus.UNAUTHORIZED, e.message ?: "Unauthorized")

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun invalid(e: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val details = e.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "invalid") }
        return build(HttpStatus.BAD_REQUEST, "Validation failed", details)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class, MethodArgumentTypeMismatchException::class)
    fun badInput(e: Exception) = build(HttpStatus.BAD_REQUEST, "Malformed request or invalid parameter value")

    // @ExceptionHandler only accepts Throwable types, and ErrorResponse is an interface.
    // Spring's own web exceptions (unknown route, 405, 415, ...) implement ErrorResponse and carry their status.
    @ExceptionHandler(Exception::class)
    fun fallback(e: Exception): ResponseEntity<ApiError> {
        if (e is ErrorResponse) {
            val status = HttpStatus.resolve(e.statusCode.value()) ?: HttpStatus.INTERNAL_SERVER_ERROR
            val message = e.body.detail ?: status.reasonPhrase
            return ResponseEntity.status(status).headers(e.headers).body(apiError(status, message))
        }
        log.error("Unexpected error while processing request", e)
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error")
    }

    private fun build(status: HttpStatus, message: String, details: Map<String, String> = emptyMap()) =
        ResponseEntity.status(status).body(apiError(status, message, details))

    private fun apiError(status: HttpStatus, message: String, details: Map<String, String> = emptyMap()) =
        ApiError(status.value(), status.reasonPhrase, message, details)
}
