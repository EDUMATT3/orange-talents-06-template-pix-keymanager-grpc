package br.com.edumatt3.pix.integration

import br.com.edumatt3.pix.register.CustomerAccount
import com.fasterxml.jackson.annotation.JsonProperty

data class CustomerAccountResponse(
    @field:JsonProperty("tipo")
    val accountType: String,
    @field:JsonProperty("instituicao")
    val institution: Institution,
    @field:JsonProperty("agencia")
    val agency: String,
    @field:JsonProperty("numero")
    val accountNumber: String,
    @field:JsonProperty("titular")
    val bearer: Bearer,
) {
        fun toModel() = CustomerAccount(
            bank = institution.name,
            agency = agency,
            accountNumber = accountNumber,
            customerName = bearer.name,
            customerCpf = bearer.cpf
        )
}

data class Institution(
    @field:JsonProperty("nome")
    val name: String,
    val ispb: String
)

class Bearer(
    val id: String,
    @field:JsonProperty("nome")
    val name: String,
    val cpf: String
)
