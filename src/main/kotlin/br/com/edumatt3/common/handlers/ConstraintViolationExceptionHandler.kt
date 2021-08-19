package br.com.edumatt3.common.handlers

import br.com.edumatt3.common.ExceptionHandler
import br.com.edumatt3.common.ExceptionHandler.*
import com.google.protobuf.Any
import com.google.rpc.BadRequest
import com.google.rpc.Code
import com.google.rpc.Status
import javax.inject.Singleton
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

@Singleton
class ConstraintViolationExceptionHandler: ExceptionHandler<ConstraintViolationException> {

    override fun handle(e: ConstraintViolationException): StatusWithDetails {

        val details = BadRequest.newBuilder()
            .addAllFieldViolations(e.constraintViolations.map(constrainToField))
            .build()

        val statusProto = Status.newBuilder()
            .setCode(Code.INVALID_ARGUMENT_VALUE)
            .setMessage("Request with invalid data")
            .addDetails(Any.pack(details))
            .build()

        return StatusWithDetails(statusProto)
    }

    override fun supports(e: Exception): Boolean = e is ConstraintViolationException

    private val constrainToField: (ConstraintViolation<*>) -> BadRequest.FieldViolation = {
        BadRequest.FieldViolation.newBuilder()
            .setField(it.propertyPath?.last()?.name ?: "? key ?")
            .setDescription(it.message)
            .build()
    }
}