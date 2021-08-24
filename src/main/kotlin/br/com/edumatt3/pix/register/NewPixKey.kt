package br.com.edumatt3.pix.register

import br.com.edumatt3.pix.AccountType
import br.com.edumatt3.pix.CustomerAccount
import br.com.edumatt3.pix.KeyType
import br.com.edumatt3.pix.PixKey
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class NewPixKey(
    @field:NotBlank
    val clientId: String,
    @field:NotNull //if is Unknown, we receive null and bean validation throws a exception
    val keyType: KeyType?,
    @field:Size(max = 77)
    val key: String?,
    @field:NotNull
    val accountType: AccountType?
) {

    fun toModel(account: CustomerAccount): PixKey {
        return PixKey(
            clientId = clientId,
            keyType = keyType!!, //if is null, validation throws an exception
            key = if (keyType == KeyType.RANDOM) UUID.randomUUID().toString() else key!!,
            accountType = accountType!!,
            account = account
        )
    }
}
