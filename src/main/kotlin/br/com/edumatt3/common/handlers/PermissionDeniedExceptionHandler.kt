package br.com.edumatt3.common.handlers

import br.com.edumatt3.common.ExceptionHandler
import br.com.edumatt3.common.ExceptionHandler.StatusWithDetails
import br.com.edumatt3.common.exceptions.PermissionDeniedException
import com.google.rpc.Code
import com.google.rpc.Status

class PermissionDeniedExceptionHandler: ExceptionHandler<PermissionDeniedException> {
    override fun handle(e: PermissionDeniedException): StatusWithDetails {

        val status = Status.newBuilder().setCode(Code.PERMISSION_DENIED.number).setMessage(e.message).build()

        return StatusWithDetails(status)
    }

    override fun supports(e: Exception): Boolean = e is PermissionDeniedException
}