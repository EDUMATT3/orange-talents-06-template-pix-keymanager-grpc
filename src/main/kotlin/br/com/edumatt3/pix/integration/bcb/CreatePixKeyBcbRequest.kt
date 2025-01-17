package br.com.edumatt3.pix.integration.bcb

import br.com.edumatt3.pix.AccountType
import br.com.edumatt3.pix.CustomerAccount
import br.com.edumatt3.pix.KeyType
import br.com.edumatt3.pix.PixKey

data class CreatePixKeyBcbRequest(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
){
    companion object {
        fun from(pixKey: PixKey): CreatePixKeyBcbRequest {
            return CreatePixKeyBcbRequest(
                keyType = pixKey.keyType,
                key = pixKey.key,
                bankAccount = BankAccount(
                    participant = CustomerAccount.ITAU_ISPB,
                    branch = pixKey.account.agency,
                    accountNumber = pixKey.account.accountNumber,
                    accountType = AccountTypeBcb.from(pixKey.accountType)
                ),
                owner = Owner(
                    type = Owner.OwnerType.LEGAL_PERSON,
                    name = pixKey.account.customerName,
                    taxIdNumber = pixKey.account.customerCpf
                )
            )
        }
    }
}

data class BankAccount(
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

data class Owner(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
){
    enum class OwnerType {
        NATURAL_PERSON, LEGAL_PERSON
    }
}
