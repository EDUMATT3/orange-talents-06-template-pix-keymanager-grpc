package br.com.edumatt3.pix.register

import br.com.edumatt3.CreatePixKeyRequest
import br.com.edumatt3.CreatePixKeyResponse
import br.com.edumatt3.CreatePixKeyServiceGrpc
import br.com.edumatt3.common.ExceptionHandlerAround
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
@ExceptionHandlerAround
class CreatePixKeyEndpoint(private val newPixKeyService: NewPixKeyService) : CreatePixKeyServiceGrpc.CreatePixKeyServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun register(
        request: CreatePixKeyRequest?,
        responseObserver: StreamObserver<CreatePixKeyResponse>?
    ) {

        logger.info("nova requisição: {}", request)

        val newPixKey = request!!.toModel()
        val pixKey = newPixKeyService.register(newPixKey)

        logger.info("Pix created with id: {}", pixKey)
        val response = CreatePixKeyResponse.newBuilder().setPixId(pixKey).build()

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}


