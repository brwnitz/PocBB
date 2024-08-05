package com.example.pocbb.Presenter

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.poc_bb.Models.PixChargeRequest
import com.example.poc_bb.Services.PixManager
import com.example.poc_bb.Utils.QRCodeUtil
import com.example.poc_bb.Utils.SharedPreferencesUtil
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