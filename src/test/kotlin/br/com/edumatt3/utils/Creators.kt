package br.com.edumatt3.utils

import br.com.edumatt3.pix.integration.itauerp.Bearer
import br.com.edumatt3.pix.integration.itauerp.CustomerAccountResponse
import br.com.edumatt3.pix.integration.itauerp.Institution
import br.com.edumatt3.pix.AccountType
import br.com.edumatt3.pix.KeyType
import br.com.edumatt3.pix.PixKey
import br.com.edumatt3.pix.integration.bcb.AccountTypeBcb
import br.com.edumatt3.pix.integration.bcb.BankAccount
import br.com.edumatt3.pix.integration.bcb.Owner
import br.com.edumatt3.pix.integration.bcb.PixKeyDetailsResponse
import java.time.LocalDateTime

fun  createItauCustomerAccountReponse(clientId: String, cpf: String,): CustomerAccountResponse {
    return CustomerAccountResponse(
        accountType = AccountType.CONTA_CORRENTE.name,
        institution = Institution(
            "Itau",
            "xxx",
        ),
        agency = "0001",
        accountNumber = "0002",
        bearer = Bearer(
            id = clientId,
            name = "Fulano",
            cpf = cpf
        )
    )
}

fun createPixKey(clientId: String, cpf: String): PixKey {
    val customerAccount = createItauCustomerAccountReponse(clientId, cpf).toModel()
    return PixKey(
        clientId = clientId,
        keyType = KeyType.CPF,
        key = cpf,
        accountType =  AccountType.CONTA_CORRENTE,
        account = customerAccount
    )
}

fun createPixKeyDetailsResponse(key: String,): PixKeyDetailsResponse = PixKeyDetailsResponse(
    keyType = KeyType.RANDOM,
    key = key,
    bankAccount = BankAccount(
        participant = "ITAU",
        branch = "0001",
        accountNumber = "10001",
        accountType = AccountTypeBcb.CACC
    ),
    owner = Owner(Owner.OwnerType.LEGAL_PERSON, "fulano", "50508675847"),
    createdAt = LocalDateTime.now()
)
