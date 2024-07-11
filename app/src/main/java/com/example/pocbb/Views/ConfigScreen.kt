package com.example.pocbb.Views

import android.util.Log
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
import androidx.navigation.NavHostController
import com.example.poc_bb.Utils.SharedPreferencesUtil
import com.example.pocbb.R

@Composable
fun ConfigScreen(navController: NavHostController? = null, sharedPreferencesUtil: SharedPreferencesUtil) {
    val clientIdState = remember{ mutableStateOf(sharedPreferencesUtil.readData("clientId","")!!) }
    val clientSecretState = remember{ mutableStateOf(sharedPreferencesUtil.readData("clientSecret","")!!) }
    val devKeyState = remember{ mutableStateOf(sharedPreferencesUtil.readData("devKey","")!!) }
    val pixKeyState = remember{ mutableStateOf(sharedPreferencesUtil.readData("pixReceive","")!!) }
    val pixExpirationState = remember{ mutableStateOf(sharedPreferencesUtil.readData("pixExpiration","")!!) }

    val scrollState = rememberScrollState()
    Box(modifier = Modifier
        .fillMaxHeight()
        .background(Color(0xFFFFF200))
        .verticalScroll(scrollState), contentAlignment = Alignment.Center) {
        Column (modifier = Modifier.fillMaxSize()){
            Column(modifier = Modifier
                .padding(top = 160.dp, start = 25.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                Icon(painter = painterResource(id = R.drawable.sort_arrow), contentDescription = "icon_connection", modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                CustomInput(labelName = "Client ID", onValueChange = {newValue -> clientIdState.value = newValue}, text = clientIdState.value)
                CustomInput(labelName = "Client Secret", onValueChange = {newValue -> clientSecretState.value = newValue}, text = clientSecretState.value)
                CustomInput(labelName = "Dev Key", onValueChange = {newValue -> devKeyState.value = newValue}, text = devKeyState.value)
            }
            Column (modifier = Modifier
                .padding(top = 40.dp, start = 25.dp, bottom = 160.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                Icon(painter = painterResource(id = R.drawable.icon_coin), contentDescription = "icon_user", modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                CustomInput(labelName = "Chave PIX de recebimento", onValueChange = {newValue -> pixKeyState.value = newValue}, text = pixKeyState.value)
                CustomInput(labelName = "Tempo de expiração pix (segundos)", width = 200.dp, isNumber = true, onValueChange = {newValue -> pixExpirationState.value = newValue}, text = pixExpirationState.value)
            }
        }
        Button(modifier = Modifier.width(330.dp).offset((25).dp, y = (340).dp).align(Alignment.CenterStart).clip(RoundedCornerShape(10.dp)).shadow(elevation = 40.dp), contentPadding = PaddingValues(0.dp),shape = RoundedCornerShape(10.dp),
            onClick = {
                Log.d("SHARED", "Saving ${clientIdState.value} to clientId")
                sharedPreferencesUtil.saveData("clientId", clientIdState.value)
                sharedPreferencesUtil.saveData("clientSecret", clientSecretState.value)
                sharedPreferencesUtil.saveData("devKey", devKeyState.value)
                sharedPreferencesUtil.saveData("pixReceive", pixKeyState.value)
                sharedPreferencesUtil.saveData("pixExpiration", pixExpirationState.value)
            }) {
            Text(text = "Salvar", style = MaterialTheme.typography.bodySmall.copy(color = Color.White), fontWeight = FontWeight.Bold)
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
}

@Composable
fun CustomInput(text:String?, onValueChange:(String) -> Unit, labelName: String, width: Dp = 320.dp, height: Dp = 32.dp, isNumber: Boolean = false){

    Column (modifier = Modifier.padding(top = 24.dp)){
        Text(text = labelName, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        TextFieldCustom(value = text, onValueChange = onValueChange, placeholder = "", width = width, height = height, isCurrency = false, fontSize = 14.sp, alignment = Alignment.CenterStart, textAlign = TextAlign.Start, isNumber = isNumber)
    }
}
