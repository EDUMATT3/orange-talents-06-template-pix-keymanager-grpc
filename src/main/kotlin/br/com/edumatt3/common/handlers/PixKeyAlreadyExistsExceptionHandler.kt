package br.com.edumatt3.common.handlers

import br.com.edumatt3.common.ExceptionHandler
import br.com.edumatt3.common.ExceptionHandler.*
import br.com.edumatt3.common.exceptions.PixKeyAlreadyExistsException
import com.google.rpc.Code
import com.google.rpc.Status

import javax.inject.Singleton

@Singleton
class PixKeyAlreadyExistsExceptionHandler: ExceptionHandler<PixKeyAlreadyExistsException> {
    override fun handle(e: PixKeyAlreadyExistsException): StatusWithDetails {

        val status = Status.newBuilder()
            .setCode(Code.ALREADY_EXISTS.number)
            .setMessage(e.message)
            .build()

        return StatusWithDetails(status)

    }

    override fun supports(e: Exception): Boolean = e is PixKeyAlreadyExistsException
}