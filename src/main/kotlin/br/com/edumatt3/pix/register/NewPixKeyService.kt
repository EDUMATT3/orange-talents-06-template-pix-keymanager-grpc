package br.com.edumatt3.pix.register

import br.com.edumatt3.common.exceptions.PixKeyAlreadyExistsException
import br.com.edumatt3.pix.integration.ItauErpClient
import com.google.rpc.BadRequest
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class NewPixKeyService(private val itauErpClient: ItauErpClient,@Inject val pixKeyRepository: PixKeyRepository) {
    fun register(@Valid newPixKey: NewPixKey): String {

        //pode ser cadastrada duas chaves aleatórias?

        if (pixKeyRepository.existsByKey(newPixKey.key!!)) throw PixKeyAlreadyExistsException()

        val response = itauErpClient.findAccount(newPixKey.clientId, newPixKey.accountType!!)
        val account = response.body()?.toModel() ?: throw IllegalStateException("Client not found on itau erp")
        val pixKey = newPixKey.toModel(account)

        pixKeyRepository.save(pixKey)

        return pixKey.pixId
    }
}
