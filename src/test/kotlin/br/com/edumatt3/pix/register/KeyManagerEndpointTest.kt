package br.com.edumatt3.pix.register

import br.com.edumatt3.AccountTypeMessage
import br.com.edumatt3.KeyTypeMessage
import br.com.edumatt3.PixKeyManagerGrpcServiceGrpc
import br.com.edumatt3.PixKeyRequest
import br.com.edumatt3.common.exceptions.PixKeyAlreadyExistsException
import br.com.edumatt3.pix.integration.Bearer
import br.com.edumatt3.pix.integration.CustomerAccountResponse
import br.com.edumatt3.pix.integration.Institution
import br.com.edumatt3.pix.integration.ItauErpClient
import br.com.edumatt3.pix.register.AccountType.CONTA_CORRENTE
import br.com.edumatt3.utils.createItauCustomerAccountReponse
import br.com.edumatt3.utils.createPixKey
import br.com.edumatt3.utils.violations
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class KeyManagerEndpointTest(
    private val pixKeyRepository: PixKeyRepository,
    private val keyManagerClient: PixKeyManagerGrpcServiceGrpc.PixKeyManagerGrpcServiceBlockingStub,
) {

    @Inject
    lateinit var itauErpClient: ItauErpClient

    @BeforeEach
    internal fun setUp() {
        pixKeyRepository.deleteAll()
    }

    val CLIENT_ID = UUID.randomUUID().toString()
    val validCpf = "50508675847"
    val accountType = CONTA_CORRENTE

    @Test
    internal fun `should create a new pix key`() {

        `when`(itauErpClient.findAccount(CLIENT_ID, accountType))
            .thenReturn(HttpResponse.ok(createItauCustomerAccountReponse(CLIENT_ID, validCpf, accountType)))


        val pixKeyRequest = PixKeyRequest.newBuilder().setClientId(CLIENT_ID)
            .setKeyType(KeyTypeMessage.CPF)
            .setKey(validCpf)
            .setAccountType(AccountTypeMessage.CONTA_CORRENTE)
            .build()

        val response = keyManagerClient.register(pixKeyRequest)
        assertNotNull(response.pixId)
    }

    @Test
    internal fun `should not register when key already exists`() {

        pixKeyRepository.save(createPixKey(CLIENT_ID, validCpf, accountType ))

        val exception = assertThrows<StatusRuntimeException> {
            val pixKeyRequest = PixKeyRequest.newBuilder().setClientId(CLIENT_ID)
                .setKeyType(KeyTypeMessage.CPF)
                .setKey(validCpf)
                .setAccountType(AccountTypeMessage.CONTA_CORRENTE)
                .build()

            keyManagerClient.register(pixKeyRequest)
        }

        with(exception){
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals(PixKeyAlreadyExistsException().message, status.description)
        }
    }

    @Test
    internal fun `should not register when client isnt found on itau erp system`() {

        `when`(itauErpClient.findAccount(CLIENT_ID, CONTA_CORRENTE))
            .thenReturn(HttpResponse.notFound())

        val exception = assertThrows<StatusRuntimeException> {
            val pixKeyRequest = PixKeyRequest.newBuilder().setClientId(CLIENT_ID)
                .setKeyType(KeyTypeMessage.CPF)
                .setKey(validCpf)
                .setAccountType(AccountTypeMessage.CONTA_CORRENTE)
                .build()

            keyManagerClient.register(pixKeyRequest)
        }

        with(exception){
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Client not found on itau erp", status.description)
        }
    }

    @Test
    internal fun `should not register when params are invalid`() {

        val thrown = assertThrows<StatusRuntimeException> {
            keyManagerClient.register(PixKeyRequest.newBuilder().build())
        }

        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertThat(violations(), containsInAnyOrder(
                Pair("clientId", "must not be blank"),
                Pair("accountType", "must not be null"),
                Pair("keyType", "must not be null"),
            ))
        }
    }

    @Test
    internal fun `should not register when invalid key`() {
        val thrown = assertThrows<StatusRuntimeException> {
            val pixKeyRequest = PixKeyRequest.newBuilder().setClientId(CLIENT_ID)
                .setKeyType(KeyTypeMessage.CPF)
                .setKey("5050.invalid.8675847")
                .setAccountType(AccountTypeMessage.CONTA_CORRENTE)
                .build()
            keyManagerClient.register(pixKeyRequest)
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Request with invalid data", status.description)
        }

    }

    @MockBean(ItauErpClient::class)
    fun mathService(): ItauErpClient? {
        return mock(ItauErpClient::class.java)
    }

    @Factory
    class Clients  {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixKeyManagerGrpcServiceGrpc.PixKeyManagerGrpcServiceBlockingStub? {
            return PixKeyManagerGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}