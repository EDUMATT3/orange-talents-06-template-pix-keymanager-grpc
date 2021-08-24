package br.com.edumatt3.pix.consult

import br.com.edumatt3.common.exceptions.PixKeyNotFoundException
import br.com.edumatt3.pix.integration.bcb.CentralBankClient
import br.com.edumatt3.pix.PixKeyRepository
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import java.util.*
import javax.validation.constraints.NotBlank

//all the inherances are known on compilation time and another inherances cant be createds
//is abstract by self
@Introspected
sealed class PixConsultFilter {

    abstract fun filter(pixKeyRepository: PixKeyRepository, bankClient: CentralBankClient): PixKeyInfo

    @Introspected
    class PixId(
        @field:NotBlank val pixId: String,
        @field:NotBlank val clientId: String,
    ): PixConsultFilter() {
        override fun filter(pixKeyRepository: PixKeyRepository, bankClient: CentralBankClient): PixKeyInfo {

            return pixKeyRepository.findById(UUID.fromString(pixId))
                .filter{ it.belongsToClient(clientId) }
                .map (PixKeyInfo::from)
                .orElseThrow{ PixKeyNotFoundException() }
        }
    }

    @Introspected
    class Key(@field:NotBlank val key: String,): PixConsultFilter(){
        override fun filter(pixKeyRepository: PixKeyRepository, bankClient: CentralBankClient): PixKeyInfo {
            return pixKeyRepository.findByKey(key)
                .map (PixKeyInfo::from)
                .orElseGet {
                    val response = bankClient.getByKey(key)

                    when (response.status) {
                        HttpStatus.OK -> PixKeyInfo.from(response.body()!!)
                        else -> throw PixKeyNotFoundException()
                    }
                }
        }
    }

    @Introspected
    class Unknown: PixConsultFilter(){
        override fun filter(pixKeyRepository: PixKeyRepository, bankClient: CentralBankClient): PixKeyInfo {
            throw IllegalArgumentException("Invalid or missing pix key")
        }
    }

}
