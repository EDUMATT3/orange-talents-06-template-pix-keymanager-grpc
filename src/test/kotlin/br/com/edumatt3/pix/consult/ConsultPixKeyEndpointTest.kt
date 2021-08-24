package br.com.edumatt3.pix.consult

import br.com.edumatt3.*
import br.com.edumatt3.pix.PixKeyRepository
import br.com.edumatt3.pix.integration.bcb.BankAccount
import br.com.edumatt3.pix.integration.bcb.CentralBankClient
import br.com.edumatt3.pix.integration.bcb.PixKeyDetailsResponse
import br.com.edumatt3.utils.createPixKey
import br.com.edumatt3.utils.createPixKeyDetailsResponse
import com.google.rpc.Code
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import org.mockito.Mockito.`when`

@MicronautTest(transactional = false)
internal class ConsultPixKeyEndpointTest(private val repository: PixKeyRepository) {

    @Inject
    private lateinit var client: ConsultPixKeyServiceGrpc.ConsultPixKeyServiceBlockingStub

    @Inject
    private lateinit var bankClient: CentralBankClient

    private val clientId = UUID.randomUUID().toString()
    private val cpf = "00000000000";
    private val savedPixKey = createPixKey(clientId, cpf)

    @BeforeEach
    internal fun setUp() {

        savedPixKey.updateRandomKey("", LocalDateTime.now())
        repository.save(savedPixKey)
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    internal fun `should return a valid pix key by id and client id`() {

        val request = ConsultPixKeyRequest.newBuilder()
            .setPixId(
                ConsultPixKeyRequest.FilterByPixId.newBuilder()
                    .setPixId(savedPixKey.id.toString())
                    .setClientId(clientId)
                    .build()
            )
            .build()

        val response = client.consult(request)

        with(response){
            assertEquals(savedPixKey.id.toString(), this.pixId)
            assertEquals(savedPixKey.key, this.pixKey.key)
            assertEquals(savedPixKey.accountType.name, this.pixKey.account.type.name)
        }
    }

    @Test
    internal fun `should return a valid pix key by key`() {

        val request = ConsultPixKeyRequest.newBuilder()
            .setKey(savedPixKey.key)
            .build()

        val response = client.consult(request)

        with(response){
            assertEquals(savedPixKey.id.toString(), this.pixId)
            assertEquals(savedPixKey.key, this.pixKey.key)
            assertEquals(savedPixKey.accountType.name, this.pixKey.account.type.name)
        }
    }

    @Test
    internal fun `should return a valid pix key from bcb sytem`() {

        val anyKey = UUID.randomUUID().toString()
        `when`(bankClient.getByKey(anyKey)).thenReturn(HttpResponse.ok(createPixKeyDetailsResponse(anyKey)))

        val request = ConsultPixKeyRequest.newBuilder()
            .setKey(anyKey)
            .build()

        val response = client.consult(request)

        with(response){
            assertEquals(anyKey, this.pixKey.key)
            assertEquals(AccountTypeMessage.CONTA_CORRENTE.name, this.pixKey.account.type.name)
        }
    }

    @Test
    internal fun `shouldnt return exception when keyId isnt found`() {

        val request = ConsultPixKeyRequest.newBuilder()
            .setPixId(
                ConsultPixKeyRequest.FilterByPixId.newBuilder()
                .setPixId(UUID.randomUUID().toString())
                .setClientId("xxx")
                .build())
            .build()

        val thrown = assertThrows<StatusRuntimeException> {
            val response = client.consult(request)
        }

        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("pix key not found", status.description)
        }
    }

    @Test
    internal fun `shouldnt return exception when key isnt found on cbc`() {

        val anyKey = UUID.randomUUID().toString()
        `when`(bankClient.getByKey(anyKey)).thenReturn(HttpResponse.notFound())

        val request = ConsultPixKeyRequest.newBuilder()
            .setKey(anyKey)
            .build()

        val thrown = assertThrows<StatusRuntimeException> {
            val response = client.consult(request)
        }

        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("pix key not found", status.description)
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ConsultPixKeyServiceGrpc.ConsultPixKeyServiceBlockingStub? {
            return ConsultPixKeyServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(CentralBankClient::class)
    fun centralBankinClient(): CentralBankClient? {
        return Mockito.mock(CentralBankClient::class.java)
    }
}