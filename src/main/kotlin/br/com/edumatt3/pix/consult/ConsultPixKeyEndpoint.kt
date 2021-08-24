package br.com.edumatt3.pix.consult

import br.com.edumatt3.ConsultPixKeyRequest
import br.com.edumatt3.ConsultPixKeyResponse
import br.com.edumatt3.ConsultPixKeyServiceGrpc
import br.com.edumatt3.common.ExceptionHandlerAround
import br.com.edumatt3.pix.PixKeyRepository
import br.com.edumatt3.pix.integration.bcb.CentralBankClient
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ExceptionHandlerAround
class ConsultPixKeyEndpoint(
    private val pixKeyRepository: PixKeyRepository,
    private val bankClient: CentralBankClient
): ConsultPixKeyServiceGrpc.ConsultPixKeyServiceImplBase() {

    override fun consult(request: ConsultPixKeyRequest?, responseObserver: StreamObserver<ConsultPixKeyResponse>?) {

        val filter = request.toFilter()
        val pixKeyInfo = filter.filter(pixKeyRepository, bankClient)
        val response = pixKeyInfo.toConsultPixKeyResponse()
        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}
