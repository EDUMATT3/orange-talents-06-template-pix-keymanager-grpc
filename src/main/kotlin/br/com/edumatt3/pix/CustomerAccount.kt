package br.com.edumatt3.pix

import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank

@Embeddable
class CustomerAccount(
    @field:NotBlank
    val bank: String,
    @field:NotBlank
    val agency: String,
    @field:NotBlank
    val accountNumber: String,
    @field:NotBlank
    val customerName: String,
    @field:NotBlank
    val customerCpf: String
){
    companion object {
        public val ITAU_ISPB: String = "60701190"
    }
}