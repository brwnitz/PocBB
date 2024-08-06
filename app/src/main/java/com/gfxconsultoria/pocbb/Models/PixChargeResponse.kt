package com.gfxconsultoria.pocbb.Models

class PixChargeResponse {
    var valor : Valor? = null
    var chave : String? = null
    var solicitacaoPagador : String? = null
    var infoAdicionais : List<InfoAdicionais>? = null
    var calendario : Calendario? = null
    var devedor : Devedor? = null
    var txid : String? = null
    var revisao : Int? = null
    var location : String? = null
    var pixCopiaECola : String? = null
}