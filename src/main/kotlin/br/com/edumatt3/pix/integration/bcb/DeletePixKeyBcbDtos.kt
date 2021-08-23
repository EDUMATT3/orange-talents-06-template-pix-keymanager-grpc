package br.com.edumatt3.pix.integration.bcb

import br.com.edumatt3.pix.register.CustomerAccount
import java.time.LocalDateTime

data class DeletePixKeyBcbRequest(
    val key: String,
    val participant: String = CustomerAccount.ITAU_ISPB
)

data class DeletePixKeyBcbResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime,
)
