package br.com.edumatt3.common

import io.grpc.BindableService
import io.grpc.stub.StreamObserver
import io.micronaut.aop.MethodInvocationContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
internal class ExceptionHandlerInterceptorTest {

    @Mock
    lateinit var context: MethodInvocationContext<BindableService, Any?>

    private val interceptor = ExceptionHandlerInterceptor(resolver = ExceptionHandlerResolver(handlers = emptyList()))

    @Test
    internal fun `Should generate an error on grpc response`(@Mock streamObserver: StreamObserver<*>) {

        with(context) {
            `when`(proceed()).thenThrow(RuntimeException("abacate"))
            //streamObs está na posição 1
            `when`(parameterValues).thenReturn(arrayOf(null, streamObserver))
        }

        interceptor.intercept(context)

        verify(streamObserver).onError(notNull())
    }

    @Test
    fun `should return the same response`() {
        val expected = "any..."

        `when`(context.proceed()).thenReturn(expected)

        assertEquals(expected, interceptor.intercept(context))
    }
}