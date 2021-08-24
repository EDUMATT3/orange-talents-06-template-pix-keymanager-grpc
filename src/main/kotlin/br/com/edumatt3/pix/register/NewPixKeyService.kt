package br.com.edumatt3.pix.register

import br.com.edumatt3.common.exceptions.PixKeyAlreadyExistsException
import br.com.edumatt3.pix.PixKeyRepository
import br.com.edumatt3.pix.integration.bcb.CentralBankClient
import br.com.edumatt3.pix.integration.bcb.CreatePixKeyBcbRequest
import br.com.edumatt3.pix.integration.itauerp.ItauErpClient
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class NewPixKeyService(
    private val itauErpClient: ItauErpClient,
    private val pixKeyRepository: PixKeyRepository,
    private val centralBankClient: CentralBankClient
) {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    fun register(@Valid newPixKey: NewPixKey): String {

        //pode ser cadastrada duas chaves aleatórias?

        if (pixKeyRepository.existsByKey(newPixKey.key!!)) throw PixKeyAlreadyExistsException()

        LOGGER.info("looking for customer account in itau erp system")
        val response = itauErpClient.findAccount(newPixKey.clientId, newPixKey.accountType!!)
        val account = response.body()?.toModel() ?: throw IllegalStateException("Client not found on itau erp")

        val pixKey = newPixKey.toModel(account)

        LOGGER.info("registering pix key in central bank (BCB)")
        val bcbResponse = centralBankClient.createPixKey(CreatePixKeyBcbRequest.from(pixKey))

        when(bcbResponse.status){
           HttpStatus.OK -> pixKey.updateRandomKey(bcbResponse.body()!!.key, bcbResponse.body()!!.createdAt)
            else -> throw IllegalStateException("Error trying to create pix key in central bank (BCB)")
        }

        pixKeyRepository.save(pixKey)

        return pixKey.id.toString()
    }
}
