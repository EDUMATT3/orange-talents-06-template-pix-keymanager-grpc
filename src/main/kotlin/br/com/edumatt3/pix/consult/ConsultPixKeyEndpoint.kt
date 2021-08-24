package br.com.edumatt3.pix.consult

import br.com.edumatt3.ConsultPixKeyRequest
import br.com.edumatt3.ConsultPixKeyRequest.FilterCase.KEY
import br.com.edumatt3.ConsultPixKeyRequest.FilterCase.PIXID
import br.com.edumatt3.ConsultPixKeyResponse
import br.com.edumatt3.PixKeyConsultServiceGrpc
import br.com.edumatt3.common.ExceptionHandlerAround
import br.com.edumatt3.pix.integration.bcb.CentralBankClient
import br.com.edumatt3.pix.PixKeyRepository
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ExceptionHandlerAround
class ConsultPixKeyEndpoint(
    private val pixKeyRepository: PixKeyRepository,
    private val bankClient: CentralBankClient
): PixKeyConsultServiceGrpc.PixKeyConsultServiceImplBase() {

    override fun consult(request: ConsultPixKeyRequest?, responseObserver: StreamObserver<ConsultPixKeyResponse>?) {

        val filter = request.toFilter()
        val pixKeyInfo = filter.filter(pixKeyRepository, bankClient)

        responseObserver?.onNext(pixKeyInfo.toConsultPixKeyResponse())
        responseObserver?.onCompleted()
    }
}
