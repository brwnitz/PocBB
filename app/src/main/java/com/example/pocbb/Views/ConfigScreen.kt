package com.example.pocbb.Views

import android.content.Context
import android.content.pm.PackageManager
import android.health.connect.datatypes.units.Length
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.poc_bb.Utils.SharedPreferencesUtil
import com.example.pocbb.Models.Scanned
import com.example.pocbb.Models.ScannedModel
import com.example.pocbb.R
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

@Composable
fun ConfigScreen(navController: NavHostController? = null, sharedPreferencesUtil: SharedPreferencesUtil, context: Context) {
    val scanned = remember {
        Scanned().apply {
            client_id.value = sharedPreferencesUtil.readData("clientId", "")
            client_secret.value = sharedPreferencesUtil.readData("clientSecret", "")
            dev_key.value = sharedPreferencesUtil.readData("devKey", "")
            pix_key.value = sharedPreferencesUtil.readData("pixReceive", "")
            expiration.value = sharedPreferencesUtil.readData("pixExpiration", "")
        }
    }
    val showScanner = remember { mutableStateOf(false)}

    val scrollState = rememberScrollState()
    Box(modifier = Modifier
        .fillMaxHeight()
        .background(Color(0xFFFFF200))
        .verticalScroll(scrollState), contentAlignment = Alignment.Center) {
        Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally){
            Column(modifier = Modifier
                .padding(top = 160.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                Icon(painter = painterResource(id = R.drawable.sort_arrow), contentDescription = "icon_connection", modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                CustomInput(labelName = "Client ID", onValueChange = {newValue -> scanned.client_id.value = newValue}, text = scanned.client_id.value)
                CustomInput(labelName = "Client Secret", onValueChange = {newValue -> scanned.client_secret.value = newValue}, text = scanned.client_secret.value)
                CustomInput(labelName = "Dev Key", onValueChange = {newValue -> scanned.dev_key.value = newValue}, text = scanned.dev_key.value)
            }
            Column (modifier = Modifier
                .padding(top = 40.dp, bottom = 160.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                Icon(painter = painterResource(id = R.drawable.icon_coin), contentDescription = "icon_user", modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                CustomInput(labelName = "Chave PIX de recebimento", onValueChange = {newValue -> scanned.pix_key.value = newValue}, text = scanned.pix_key.value)
                CustomInput(labelName = "Tempo de expiração pix (segundos)", width = 160.dp, isNumber = true, onValueChange = {newValue -> scanned.expiration.value = newValue}, text = scanned.expiration.value)
            }
        }
        Button(modifier = Modifier
            .width(260.dp)
            .height(44.dp)
            .offset(y = (340).dp)
            .align(Alignment.Center)
            .clip(RoundedCornerShape(10.dp))
            .shadow(elevation = 40.dp), contentPadding = PaddingValues(0.dp),shape = RoundedCornerShape(10.dp),
            onClick = {
                Log.d("SHARED", "Saving ${scanned.client_id} to clientId")
                sharedPreferencesUtil.saveData("clientId", scanned.client_id.value!!)
                sharedPreferencesUtil.saveData("clientSecret", scanned.client_secret.value!!)
                sharedPreferencesUtil.saveData("devKey", scanned.dev_key.value!!)
                sharedPreferencesUtil.saveData("pixReceive", scanned.pix_key.value!!)
                sharedPreferencesUtil.saveData("pixExpiration", scanned.expiration.value!!)
            }) {
            Text(text = "Salvar", style = MaterialTheme.typography.bodySmall.copy(color = Color.White), fontWeight = FontWeight.Bold)
        }
        Button(modifier = Modifier
            .width(260.dp)
            .height(44.dp)
            .offset(y = (400).dp)
            .align(Alignment.Center)
            .clip(RoundedCornerShape(10.dp))
            .shadow(elevation = 40.dp), contentPadding = PaddingValues(0.dp),shape = RoundedCornerShape(10.dp),
            onClick = {
                showScanner.value = true
            }) {
            Text(text = "Ler QRCode", style = MaterialTheme.typography.bodySmall.copy(color = Color.White), fontWeight = FontWeight.Bold)
        }
    IconButton(onClick = { navController?.navigate("main") },  modifier = Modifier
        .size(42.dp)
        .align(Alignment.TopStart)
        .offset(x = 20.dp, y = 20.dp)) {
        Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "BackArrow", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(42.dp))
    }
    Image(painter = painterResource(id = R.drawable.image_5), contentDescription = "logo_bb_top", modifier = Modifier
        .size(180.dp)
        .offset(x = 16.dp, y = 0.dp)
        .align(Alignment.TopEnd))
    Image(painter = painterResource(id = R.drawable.image_4), contentDescription = "logo_bb_bottom", modifier = Modifier
        .size(180.dp)
        .offset(x = 0.dp, y = 60.dp)
        .align(Alignment.BottomStart))
    }
    if (showScanner.value){
        BarcodeScannerScreen(context = context, afterScanned = {scannedJson ->
            val scannedValue = Gson().fromJson(scannedJson, ScannedModel::class.java)
            scanned.client_id.value = scannedValue.client_id
            scanned.client_secret.value = scannedValue.client_secret
            scanned.dev_key.value = scannedValue.dev_key
            scanned.pix_key.value = scannedValue.pix_key
            scanned.expiration.value = scannedValue.expiration
            sharedPreferencesUtil.saveData("clientId", scannedValue.client_id!!)
            SharedPreferencesUtil.getInstance().saveData("pixReceive", scannedValue.pix_key!!)
            sharedPreferencesUtil.saveData("clientSecret", scannedValue.client_secret!!)
            sharedPreferencesUtil.saveData("devKey", scannedValue.dev_key!!)
            sharedPreferencesUtil.saveData("pixExpiration", scannedValue.expiration!!)
            showScanner.value = false
        })
    }
}

@Composable
fun CustomInput(text:String?, onValueChange:(String) -> Unit, labelName: String, width: Dp = 300.dp, height: Dp = 35.dp, isNumber: Boolean = false, padding: PaddingValues = PaddingValues(top = 24.dp), haveLabel: Boolean = true, backgroundColor: Color = MaterialTheme.colorScheme.surface, length: Int = 100){

    Column (modifier = Modifier.padding(padding)){
        if(haveLabel)Text(text = labelName, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        TextFieldCustom(value = text, onValueChange = onValueChange, placeholder = "", width = width, height = height, isCurrency = false, fontSize = 14.sp, alignment = Alignment.CenterStart, textAlign = TextAlign.Start, isNumber = isNumber, backgroundColor = backgroundColor, lenght = length)
    }
}

@Composable
fun ZXingScannerView(onBarcodeScanned: (String) -> Unit, context: Context){
    var hasCameraPermission by remember{ mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )
    LaunchedEffect(Unit){
        when(PackageManager.PERMISSION_GRANTED){
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) ->{
                hasCameraPermission = true
            }
            else -> cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission){
        AndroidView(modifier = Modifier.fillMaxSize(),
            factory = {ctx ->
                CompoundBarcodeView(ctx).apply {
                    decodeContinuous(object : BarcodeCallback {
                        override fun barcodeResult(result: BarcodeResult?) {
                            result?.text?.let { onBarcodeScanned(it) }
                        }
                        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
                    })
                }
            },
            update = {view ->
                view.resume()
            })
    }
    else{
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Text(text = "Precisamos de permissão para acessar a câmera", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun BarcodeScannerScreen(context: Context, afterScanned: (String) -> Unit){
    var scannedBarcode by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        ZXingScannerView(onBarcodeScanned = {barcode -> scannedBarcode = barcode}, context = context)
    }

    if (scannedBarcode.isNotEmpty()){
        Text(text = "Barcode: $scannedBarcode", color = Color.White, fontSize = 16.sp)
        afterScanned(scannedBarcode)
    }
}
