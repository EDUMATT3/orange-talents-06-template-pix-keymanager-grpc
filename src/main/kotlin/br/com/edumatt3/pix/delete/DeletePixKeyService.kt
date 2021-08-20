package br.com.edumatt3.pix.delete

import br.com.edumatt3.common.exceptions.PermissionDeniedException
import br.com.edumatt3.common.exceptions.PixKeyNotFoundException
import br.com.edumatt3.pix.register.PixKeyRepository
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Singleton
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Singleton
@Validated
class DeletePixKeyService(val pixKeyRepository: PixKeyRepository) {

    fun delete(@NotBlank pixId: String?, @NotBlank clientId: String?){

        val pixKey = pixKeyRepository.findById(UUID.fromString(pixId)).orElseThrow { PixKeyNotFoundException() }

        if (!pixKey.belongsToClient(clientId!!))
            throw PermissionDeniedException("pix key doesn't belong to the customer")

        pixKeyRepository.delete(pixKey)
    }
}
