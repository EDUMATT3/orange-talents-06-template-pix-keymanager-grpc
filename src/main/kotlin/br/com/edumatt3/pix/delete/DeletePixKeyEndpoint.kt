package br.com.edumatt3.pix.delete

import br.com.edumatt3.DeletePixKeyRequest
import br.com.edumatt3.PixKeyDeleteServiceGrpc
import br.com.edumatt3.PixKeyManagerGrpcServiceGrpc
import br.com.edumatt3.common.ExceptionHandlerAround
import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ExceptionHandlerAround
class DeletePixKeyEndpoint(val deletePixKeyService: DeletePixKeyService) : PixKeyDeleteServiceGrpc.PixKeyDeleteServiceImplBase(){
    override fun delete(request: DeletePixKeyRequest?, responseObserver: StreamObserver<Empty>?) {

        deletePixKeyService.delete(request?.pixId, request?.clientId)

        responseObserver?.onNext(Empty.newBuilder().build())
        responseObserver?.onCompleted()
    }
}