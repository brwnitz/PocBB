package com.gfxconsultoria.pocbb.Models

import androidx.compose.runtime.mutableStateOf

class Scanned{
    var client_id = mutableStateOf<String?>(null)
    var client_secret = mutableStateOf<String?>(null)
    var dev_key = mutableStateOf<String?>(null)
    var pix_key = mutableStateOf<String?>(null)
    var expiration = mutableStateOf<String?>(null)

    fun updateFromScanned(scanned: ScannedModel) {
        client_id.value = scanned.client_id
        client_secret.value = scanned.client_secret
        dev_key.value = scanned.dev_key
        pix_key.value = scanned.pix_key
        expiration.value = scanned.expiration
    }

    fun toScanned(): ScannedModel{
        return ScannedModel(
            client_id = client_id.value,
            client_secret = client_secret.value,
            dev_key = dev_key.value,
            pix_key = pix_key.value,
            expiration = expiration.value
        )
    }
}