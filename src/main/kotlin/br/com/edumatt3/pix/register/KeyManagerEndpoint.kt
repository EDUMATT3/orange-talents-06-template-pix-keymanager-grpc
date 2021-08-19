package br.com.edumatt3.pix.register

import br.com.edumatt3.PixKeyManagerGrpcServiceGrpc
import br.com.edumatt3.PixKeyRequest
import br.com.edumatt3.PixKeyResponse
import br.com.edumatt3.common.ExceptionHandlerAround
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
@ExceptionHandlerAround
class KeyManagerEndpoint(private val newPixKeyService: NewPixKeyService) : PixKeyManagerGrpcServiceGrpc.PixKeyManagerGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun register(request: PixKeyRequest?, responseObserver: StreamObserver<PixKeyResponse>?) {

        logger.info("nova requisição: {}", request)

        val newPixKey = request!!.toModel()
        val pixKey = newPixKeyService.register(newPixKey)

        logger.info("Pix created with id: {}", pixKey)
        val response = PixKeyResponse.newBuilder().setPixId(pixKey).build()

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}


