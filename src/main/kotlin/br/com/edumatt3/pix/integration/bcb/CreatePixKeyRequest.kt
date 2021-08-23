package br.com.edumatt3.pix.integration.bcb

import br.com.edumatt3.KeyTypeMessage
import br.com.edumatt3.pix.register.AccountType
import br.com.edumatt3.pix.register.CustomerAccount
import br.com.edumatt3.pix.register.KeyType
import br.com.edumatt3.pix.register.PixKey

data class CreatePixKeyRequest(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccountRequest,
    val owner: OwnerRequest,
){
    companion object {
        fun from(pixKey: PixKey): CreatePixKeyRequest {
            return CreatePixKeyRequest(
                keyType = pixKey.keyType,
                key = pixKey.key,
                bankAccount = BankAccountRequest(
                    participant = CustomerAccount.ITAU_ISPB,
                    branch = pixKey.account.agency,
                    accountNumber = pixKey.account.accountNumber,
                    accountType = AccountTypeBcb.from(pixKey.accountType)
                ),
                owner = OwnerRequest(
                    type = OwnerRequest.OwnerType.LEGAL_PERSON,
                    name = pixKey.account.customerName,
                    taxIdNumber = pixKey.account.customerCpf
                )
            )
        }
    }
}

class BankAccountRequest(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountTypeBcb,
)

enum class AccountTypeBcb {
    CACC,SVGS;

    companion object {
        fun from(accountType: AccountType) = when(accountType){
            AccountType.CONTA_CORRENTE -> CACC
            AccountType.CONTA_POUPANCA -> SVGS
        }
    }
}

data class OwnerRequest(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
){
    enum class OwnerType {
        NATURAL_PERSON, LEGAL_PERSON
    }
}
