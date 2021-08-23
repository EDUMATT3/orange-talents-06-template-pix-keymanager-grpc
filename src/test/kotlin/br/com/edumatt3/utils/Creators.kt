package br.com.edumatt3.utils

import br.com.edumatt3.pix.integration.itauerp.Bearer
import br.com.edumatt3.pix.integration.itauerp.CustomerAccountResponse
import br.com.edumatt3.pix.integration.itauerp.Institution
import br.com.edumatt3.pix.register.AccountType
import br.com.edumatt3.pix.register.KeyType
import br.com.edumatt3.pix.register.PixKey

fun  createItauCustomerAccountReponse(clientId: String, cpf: String, accountType: AccountType): CustomerAccountResponse {
    return CustomerAccountResponse(
        accountType = accountType.name,
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

fun createPixKey(clientId: String, cpf: String, accountType: AccountType): PixKey {
    val customerAccount = createItauCustomerAccountReponse(clientId, cpf, accountType).toModel()
    return PixKey(
        clientId = clientId,
        keyType = KeyType.CPF,
        key = cpf,
        accountType = accountType,
        account = customerAccount
    )
}