package br.com.edumatt3.common.handlers

import br.com.edumatt3.common.ExceptionHandler
import br.com.edumatt3.common.ExceptionHandler.StatusWithDetails
import io.grpc.Status

class DefaultExceptionHandler: ExceptionHandler<Exception> {

    override fun handle(e: Exception): StatusWithDetails {
        val status = when (e) {
            is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message)
            is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message)
            else -> Status.UNKNOWN
        }
        return StatusWithDetails(status.withCause(e))
    }

    override fun supports(e: Exception): Boolean = true
}