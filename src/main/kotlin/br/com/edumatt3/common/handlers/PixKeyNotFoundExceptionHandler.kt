package br.com.edumatt3.common.handlers

import br.com.edumatt3.common.ExceptionHandler
import br.com.edumatt3.common.ExceptionHandler.*
import br.com.edumatt3.common.exceptions.PixKeyNotFoundException
import com.google.rpc.Code
import com.google.rpc.Status
import javax.inject.Singleton

@Singleton
class PixKeyNotFoundExceptionHandler: ExceptionHandler<PixKeyNotFoundException> {
    override fun handle(e: PixKeyNotFoundException): StatusWithDetails {

        val response = Status.newBuilder().setCode(Code.NOT_FOUND.number).setMessage(e.message).build()
        return StatusWithDetails(response)
    }

    override fun supports(e: Exception): Boolean = e is PixKeyNotFoundException
}