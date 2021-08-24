package br.com.edumatt3.pix.integration.itauerp

import br.com.edumatt3.pix.AccountType
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.erp.url}")
interface ItauErpClient {

    @Get("/clientes/{clientId}/contas")
    fun findAccount(@PathVariable clientId: String, @QueryValue("tipo") accountType: AccountType): HttpResponse<CustomerAccountResponse>
}
