package br.com.edumatt3.utils

import com.google.rpc.BadRequest
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto

//to help test
fun StatusRuntimeException.violations(): List<Pair<String, String>> {

    val details = StatusProto.fromThrowable(this)
        ?.detailsList?.get(0)!!
        .unpack(BadRequest::class.java)

    return details.fieldViolationsList
        .map { it.field to it.description } // same as Pair(it.field, it.description)
}