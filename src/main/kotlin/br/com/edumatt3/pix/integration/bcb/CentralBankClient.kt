package br.com.edumatt3.pix.integration.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.annotation.Client

@Client("\${bcb.url}")
interface CentralBankClient {

    @Post(
        value = "/pix/keys",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
    fun createPixKey(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>
}