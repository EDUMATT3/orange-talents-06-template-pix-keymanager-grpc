package br.com.edumatt3.common

import br.com.edumatt3.common.handlers.DefaultExceptionHandler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ExceptionHandlerResolverTest{

    private lateinit var illegalArgumentExceptionHandler: ExceptionHandler<IllegalArgumentException>

    private lateinit var resolver: ExceptionHandlerResolver

    @BeforeEach
    internal fun setUp() {
        illegalArgumentExceptionHandler = object : ExceptionHandler<IllegalArgumentException> {
            override fun handle(e: IllegalArgumentException): ExceptionHandler.StatusWithDetails {
                TODO("Not yet implemented")
            }

            override fun supports(e: Exception): Boolean = e is IllegalArgumentException
        }

        resolver = ExceptionHandlerResolver(handlers = listOf(illegalArgumentExceptionHandler))
    }

    @Test
    internal fun `should return the correct exception handler`() {
        val resolvedHandler = resolver.resolve(IllegalArgumentException())

        assertEquals(illegalArgumentExceptionHandler, resolvedHandler)
    }

    @Test
    internal fun `should return the default exception handler`() {
        val resolvedHandler = resolver.resolve(IllegalStateException())

        assertTrue(resolvedHandler is DefaultExceptionHandler)
    }

    @Test
    internal fun `should throw a IllegalStateException`() {
        resolver = ExceptionHandlerResolver(listOf(illegalArgumentExceptionHandler, illegalArgumentExceptionHandler))

        assertThrows<IllegalStateException> {
            resolver.resolve(IllegalArgumentException())
        }
    }
}