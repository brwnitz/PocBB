package com.gfxconsultoria.pocbb.Models

class PixChargeRequest(var valor: Valor, var chave: String,var calendario: Calendario) {
    val solcnpjitacaoPagador: String ? = null
    val infoAdicionais: List<InfoAdicionais> ? = null
    val devedor: Devedor ? = null
}