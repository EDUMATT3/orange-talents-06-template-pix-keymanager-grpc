package br.com.edumatt3.pix.delete

import br.com.edumatt3.common.exceptions.PermissionDeniedException
import br.com.edumatt3.common.exceptions.PixKeyNotFoundException
import br.com.edumatt3.pix.integration.bcb.CentralBankClient
import br.com.edumatt3.pix.integration.bcb.DeletePixKeyBcbRequest
import br.com.edumatt3.pix.CustomerAccount
import br.com.edumatt3.pix.PixKeyRepository
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Singleton
import javax.validation.constraints.NotBlank

@Singleton
@Validated
class DeletePixKeyService(
    val pixKeyRepository: PixKeyRepository,
    val centralBankClient: CentralBankClient
) {

    fun delete(@NotBlank pixId: String?, @NotBlank clientId: String?){

        val pixKey = pixKeyRepository.findById(UUID.fromString(pixId)).orElseThrow { PixKeyNotFoundException() }

        if (!pixKey.belongsToClient(clientId!!))
            throw PermissionDeniedException("pix key doesn't belong to the customer")

        val deletePixKeyBcbRequest = DeletePixKeyBcbRequest(pixKey.key, CustomerAccount.ITAU_ISPB)
        val bcbResponse = centralBankClient.deletePixKey(pixKey.key, deletePixKeyBcbRequest)

        if (!bcbResponse.status.equals(HttpStatus.OK))
            throw IllegalStateException("it was not possible to delete the key in the central bank system (BCB)")

        pixKeyRepository.delete(pixKey)
    }
}
