package br.com.edumatt3.pix

import jdk.jfr.Timespan
import jdk.jfr.Timestamp
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
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

    @Id
    @GeneratedValue
    var id: UUID? = null

    var createdAt: LocalDateTime? = null

    fun belongsToClient(clientId: String): Boolean = this.clientId == clientId

    private fun isRandomKey() = keyType == KeyType.RANDOM

    fun updateRandomKey(randomKey : String, createdAt: LocalDateTime) {
        this.createdAt = createdAt
        if (isRandomKey()) key = randomKey
    }
}
