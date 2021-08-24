package br.com.edumatt3.pix

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class PixKey(
    @field:NotBlank
    val clientId: String,

    @field:NotNull
    @field:Enumerated(EnumType.STRING)
    val keyType: KeyType,
    @field:NotBlank
    var key: String,
    @field:NotNull
    @field:Enumerated(EnumType.STRING)
    val accountType: AccountType,
    @field:Embedded
    val account: CustomerAccount
) {
    fun belongsToClient(clientId: String): Boolean = this.clientId == clientId

    fun isRandomKey() = keyType == KeyType.RANDOM

    fun updateRandomKey(randomKey : String) {
        if (isRandomKey()) key = randomKey
    }

    @Id
    @GeneratedValue
    var id: UUID? = null
}
