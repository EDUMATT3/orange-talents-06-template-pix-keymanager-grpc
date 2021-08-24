package br.com.edumatt3.pix.consult

import br.com.edumatt3.ConsultPixKeyRequest

fun ConsultPixKeyRequest?.toFilter(): PixConsultFilter {

    return when(this?.filterCase){
        ConsultPixKeyRequest.FilterCase.PIXID -> PixConsultFilter.PixId(pixId = this.pixId.pixId, clientId = this.pixId.clientId)
        ConsultPixKeyRequest.FilterCase.KEY -> PixConsultFilter.Key(this.key)
        else -> PixConsultFilter.Unknown()
    }
}