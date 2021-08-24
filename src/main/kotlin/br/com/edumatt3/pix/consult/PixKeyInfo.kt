package br.com.edumatt3.pix.consult

import br.com.edumatt3.AccountTypeMessage
import br.com.edumatt3.ConsultPixKeyResponse
import br.com.edumatt3.KeyTypeMessage
import br.com.edumatt3.pix.integration.bcb.AccountTypeBcb.CACC
import br.com.edumatt3.pix.integration.bcb.AccountTypeBcb.SVGS
import br.com.edumatt3.pix.integration.bcb.PixKeyDetailsResponse
import br.com.edumatt3.pix.CustomerAccount
import br.com.edumatt3.pix.PixKey
import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

data class PixKeyInfo(
    val pixId: UUID? = null,
    val clientId: String? = null,
    val type: KeyTypeMessage,
    val key: String,
    val accountType: AccountTypeMessage,
    val account: CustomerAccount,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(pixKey: PixKey): PixKeyInfo {
            return PixKeyInfo(
                pixId = pixKey.id,
                clientId = pixKey.clientId,
                type = KeyTypeMessage.valueOf(pixKey.keyType.name),
                key = pixKey.key,
                accountType = AccountTypeMessage.valueOf(pixKey.accountType.name),
                account = pixKey.account,
                createdAt = pixKey.createdAt!!
            )
        }

        fun from(response: PixKeyDetailsResponse): PixKeyInfo {
            return PixKeyInfo(
                type =  KeyTypeMessage.valueOf(response.keyType.name),
                key = response.key,
                accountType = when (response.bankAccount.accountType) {
                    CACC -> AccountTypeMessage.CONTA_CORRENTE
                    SVGS -> AccountTypeMessage.CONTA_POUPANCA
                },
                account = CustomerAccount(
                    bank = response.bankAccount.participant,
                    agency = response.bankAccount.branch,
                    accountNumber = response.bankAccount.accountNumber,
                    customerName = response.owner.name,
                    customerCpf = response.owner.taxIdNumber
                ),
                createdAt = response.createdAt
            )
        }
    }

    fun toConsultPixKeyResponse(): ConsultPixKeyResponse{
        return ConsultPixKeyResponse.newBuilder()
            .setPixId(pixId?.toString() ?: "")
            .setClientId(clientId ?: "")
            .setPixKey(
                ConsultPixKeyResponse.PixKey.newBuilder()
                .setType(type)
                .setKey(key)
                .setCreatedAt(createdAt.let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
                .setAccount(ConsultPixKeyResponse.PixKey.AccountInfo.newBuilder()
                    .setType(accountType)
                    .setAgency(account.agency)
                    .setAccountNumber(account.accountNumber)
                    .setCustomerCpf(account.customerCpf)
                    .setInstituition(account.bank)
                    .build()
                )
            )
            .build()
    }
}
