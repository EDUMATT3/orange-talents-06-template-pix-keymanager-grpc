package br.com.edumatt3.pix.delete

import br.com.edumatt3.DeletePixKeyRequest
import br.com.edumatt3.DeletePixKeyServiceGrpc
import br.com.edumatt3.pix.AccountType
import br.com.edumatt3.pix.PixKeyRepository
import br.com.edumatt3.pix.integration.bcb.CentralBankClient
import br.com.edumatt3.pix.integration.bcb.DeletePixKeyBcbRequest
import br.com.edumatt3.utils.createPixKey
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class DeletePixKeyEndpointTest(private val repository: PixKeyRepository) {

    @Inject
    private lateinit var client: DeletePixKeyServiceGrpc.DeletePixKeyServiceBlockingStub

    @Inject
    private lateinit var centralBankClient: CentralBankClient

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `should delete a pix key`() {
        val clientId = UUID.randomUUID().toString()
        val pixKey =
            repository.save(createPixKey(clientId, "50508675847"))

        `when`(centralBankClient.deletePixKey(pixKey.key, DeletePixKeyBcbRequest(pixKey.key)))
            .thenReturn(HttpResponse.ok())

        val request = DeletePixKeyRequest.newBuilder().setPixId(pixKey.id.toString()).setClientId(clientId).build()

        assertDoesNotThrow{
                client.delete(request)
        }

        assertEquals(0L, repository.count())
    }

    @Test
    internal fun `should not delete the key when it is not found`() {
        val clientId = UUID.randomUUID().toString()
        val pixKey =
            repository.save(createPixKey(clientId, "50508675847"))

        val request = DeletePixKeyRequest.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setClientId(UUID.randomUUID().toString())
            .build()

        val thrown = assertThrows<StatusRuntimeException> {
            client.delete(request)
        }

        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("pix key not found", status.description)
        }

        assertTrue(repository.count() > 0)
    }

    @Test
    internal fun `should not delete a key when not belong to the user`() {
        val pixKey =
            repository.save(createPixKey(UUID.randomUUID().toString(), "50508675847"))

        val request = DeletePixKeyRequest
            .newBuilder()
            .setPixId(pixKey.id.toString())
            .setClientId(UUID.randomUUID().toString())
            .build()

        val thrown = assertThrows<StatusRuntimeException> {
            client.delete(request)
        }

        with(thrown){
            assertEquals(Status.PERMISSION_DENIED.code, status.code)
            assertEquals("pix key doesn't belong to the customer", status.description)
        }

        assertTrue(repository.count() == 1L)
    }

    @Test
    internal fun `should not delete when it was not possible remove on bcb system`() {
        val clientId = UUID.randomUUID().toString()
        val pixKey = repository.save(
            createPixKey(
                clientId,
                "50508675847"
            )
        )

        `when`(centralBankClient.deletePixKey(pixKey.key, DeletePixKeyBcbRequest(pixKey.key)))
            .thenReturn(HttpResponse.notFound())

        val request = DeletePixKeyRequest.newBuilder()
            .setPixId(pixKey.id.toString())
            .setClientId(clientId)
            .build()

        val thrown = assertThrows<StatusRuntimeException> {
            client.delete(request)
        }

        with(thrown){
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("it was not possible to delete the key in the central bank system (BCB)", status.description)
        }

        assertTrue(repository.count() > 0)
    }

    @Factory
    class Clients  {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): DeletePixKeyServiceGrpc.DeletePixKeyServiceBlockingStub? {
            return DeletePixKeyServiceGrpc.newBlockingStub(channel)
        }
    }
    @MockBean(CentralBankClient::class)
    fun centralBankinClient(): CentralBankClient? {
        return Mockito.mock(CentralBankClient::class.java)
    }
}