package com.example.poc_bb.Utils

import android.graphics.Bitmap
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder

class QRCodeUtil {
    fun generateQRCode(pixPayloadUrl: String): Bitmap? {
        if (pixPayloadUrl.isEmpty() || pixPayloadUrl == "") return null
        val qrgEncoder = QRGEncoder(pixPayloadUrl, null, QRGContents.Type.TEXT, 500)
        return try {
            qrgEncoder.bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}