package br.com.edumatt3.pix.register

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
    val key: String,
    @field:NotNull
    @field:Enumerated(EnumType.STRING)
    val accountType: AccountType,
    @field:Embedded
    val account: CustomerAccount
) {

    @Id
    @GeneratedValue
    var id: Long? = null

    @NotNull
    val pixId: String = UUID.randomUUID().toString()
}