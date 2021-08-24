package br.com.edumatt3.pix.integration.bcb

import br.com.edumatt3.pix.register.CustomerAccount
import br.com.edumatt3.pix.register.KeyType
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

data class PixKeyDetailsResponse(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
)