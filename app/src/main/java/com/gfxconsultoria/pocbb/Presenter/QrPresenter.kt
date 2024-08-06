package com.gfxconsultoria.pocbb.Presenter

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.gfxconsultoria.pocbb.Models.PixChargeRequest
import com.gfxconsultoria.pocbb.Services.PixManager
import com.gfxconsultoria.pocbb.Utils.QRCodeUtil
import kotlinx.coroutines.launch

class QrPresenter(private val pixManager: PixManager, private val context: Context) : ViewModel() {
    var qrCodeBitmap = mutableStateOf<Bitmap?>(null)
        private set

    suspend fun createPixAndQR(pixChargeRequest: PixChargeRequest, navController: NavHostController? = null, onSuccess: () -> Unit, onError: (Boolean) -> Unit){
            val qrCodeUtil = QRCodeUtil()
            pixManager.createPixKey(pixChargeRequest, navController!!, onSuccess, onError, updatePix = { qrCode ->
                if (qrCode != null && qrCode != "") {
                    qrCodeBitmap.value = qrCodeUtil.generateQRCode(qrCode)
            }})
    }
}