package br.com.edumatt3.pix.register


import br.com.edumatt3.AccountTypeMessage.UNSPECIFIED
import br.com.edumatt3.CreatePixKeyRequest
import br.com.edumatt3.KeyTypeMessage.UNKNOWN
import br.com.edumatt3.pix.AccountType
import br.com.edumatt3.pix.KeyType

fun CreatePixKeyRequest.toModel(): NewPixKey = NewPixKey(
    clientId,
    keyType = if (keyType.equals(UNKNOWN)) null else KeyType.valueOf(keyType.name),
    key,
    accountType = if (accountType.equals(UNSPECIFIED)) null else AccountType.valueOf(accountType.name)
)