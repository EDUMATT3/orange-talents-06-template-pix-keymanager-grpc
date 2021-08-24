package br.com.edumatt3.pix.integration.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${bcb.url}")
interface CentralBankClient {

    @Post(
        value = "/pix/keys",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
    fun createPixKey(@Body request: CreatePixKeyBcbRequest): HttpResponse<CreatePixKeyResponse>

    @Delete(
        value = "/pix/keys/{key}",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
    fun deletePixKey(@PathVariable key: String, @Body request: DeletePixKeyBcbRequest): HttpResponse<DeletePixKeyBcbResponse>

    @Get(
        value = "/pix/keys/{key}",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
    fun getByKey(@PathVariable key: String): HttpResponse<PixKeyDetailsResponse>
}