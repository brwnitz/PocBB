package com.example.pocbb.Views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.poc_bb.Models.Calendario
import com.example.poc_bb.Models.Devedor
import com.example.poc_bb.Models.InfoAdicionais
import com.example.poc_bb.Models.PixChargeRequest
import com.example.poc_bb.Models.Valor
import com.example.poc_bb.Utils.SharedPreferencesUtil
import com.example.pocbb.Presenter.MainPresenter
import com.example.pocbb.Presenter.QrPresenter
import com.example.pocbb.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MainScreen(navController: NavHostController? = null, sharedPreferencesUtil: SharedPreferencesUtil, qrPresenter: QrPresenter, pixChargeRequest: PixChargeRequest? = null){
    var showSplash by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = true) {
        delay(3200)
        showSplash = false
    }

    AnimatedVisibility(visible = showSplash, enter = fadeIn(initialAlpha = 0.1f), exit = fadeOut(targetAlpha = 0f)) {
        SplashScreen()
    }
    AnimatedVisibility(visible = !showSplash, enter = fadeIn(initialAlpha = 0.1f), exit = fadeOut(targetAlpha = 0f)) {
        MainContent(navController, sharedPreferencesUtil, qrPresenter)
    }
}

@Composable
fun SplashScreen(){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF200))
            .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Image(painter = painterResource(id = R.drawable.image_1), contentDescription = "", Modifier.width(265.dp))
            TimedLinearProgress(progressDuration = 3000L)
        }
    }
}

@Composable
fun MainContent(navController: NavHostController?= null, sharedPreferencesUtil: SharedPreferencesUtil, qrPresenter: QrPresenter){
    var text by remember {
        mutableStateOf("")
    }
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF200))
            .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text(text = "Insira o valor", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.padding(bottom = 30.dp))
            TextFieldCustom(value = text, onValueChange = { text = it }, placeholder = "R$ 0,00", width = 320.dp, height = 40.dp, isCurrency = true, fontSize = 22.sp, alignment = Alignment.Center, textAlign = TextAlign.Center)
            IconButton(onClick = {
                coroutineScope.launch {
                val pixChargeRequest = PixChargeRequest(
                    valor = Valor(realToDouble(text)),
                    calendario = Calendario(sharedPreferencesUtil.readData("pixExpiration","")!!.toInt()),
                    chave = sharedPreferencesUtil.readData("pixReceive","")!!)
                qrPresenter.createPixAndQR(pixChargeRequest, navController!!)
                                 } },
                modifier = Modifier
                    .padding(top = 50.dp, bottom = 10.dp)
                    .height(100.dp)
                    .width(100.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(15.dp))
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Image(painter = painterResource(id = R.drawable.qr_code), contentDescription = "QR Code", modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp))
            }
            Text(text = "QR Code", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
        IconButton(onClick = { navController?.navigate("config") }, modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = 20.dp, y = 20.dp)) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
        }
        Image(painter = painterResource(id = R.drawable.image_3), contentDescription = "logo_bb", modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = (-20).dp, y = (10).dp)
            .size(80.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldCustom(value: String?, onValueChange: (String) -> Unit, placeholder: String, width: Dp, height: Dp, isCurrency: Boolean, fontSize: TextUnit, alignment: Alignment, textAlign: TextAlign, isNumber: Boolean = false){
    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(text = value!!, selection = TextRange(value.length)))
    }
    BasicTextField(
        value = textFieldValueState,
        onValueChange = {
                newValue ->
                if(isCurrency) {
                    val numericValue = newValue.text.filter { it.isDigit() }
                    val number = numericValue.toLongOrNull() ?: 0L
                    val formattedValue = formatToReal(number)
                    textFieldValueState = TextFieldValue(
                        text = formattedValue,
                        selection = TextRange(formattedValue.length)
                    )
                    onValueChange(formattedValue)
                }
                else if (isNumber){
                    val numericValue = newValue.text.filter { it.isDigit() }
                    textFieldValueState = TextFieldValue(
                        text = numericValue,
                        selection = TextRange(numericValue.length)
                    )
                    onValueChange(numericValue)
                }
                else{
                    textFieldValueState = newValue
                    onValueChange(newValue.text)
                }
        },
        textStyle = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            textAlign = textAlign,
            fontSize = fontSize
        ),
        singleLine = true,
        decorationBox = {
                innerTextField ->
            Box(
                modifier = Modifier
                    .width(width)
                    .height(height)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                contentAlignment = alignment,
            ) {
                if (textFieldValueState.text.isEmpty()){
                    Text(text = placeholder, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = fontSize, style = MaterialTheme.typography.bodySmall)
                }
                else innerTextField()
            }
        },
        keyboardOptions = isNumber.let {
            if (it) {
                KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            } else {
                KeyboardOptions.Default
            }
        },
        modifier = Modifier.shadow(elevation = 6.dp, shape = RoundedCornerShape(10.dp)))
}

fun formatToReal(value: Long): String{
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val inReal = value/100.0
    return numberFormat.format(inReal)
}

fun realToDouble(value:String):String{
    val numericValue = value.replace("R$","").replace(",","").replace(".","").trim()
    val decimal = numericValue.toLongOrNull()?.div(100.0)
    return decimal.toString()
}

@Composable
fun TimedLinearProgress(progressDuration: Long){
    var progress by remember { mutableStateOf(0f) }
    val totalFrames = 60*(progressDuration/1000)
    val timePerFrame = 1000L/60
    val progressIncrement = 1f/totalFrames

    LaunchedEffect(key1 = true) {
        while (progress < 1f){
            progress += progressIncrement
            delay(timePerFrame)
        }
    }
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color.Transparent
                    ),
                )
            )
            .height(18.dp)
            .width(180.dp)
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .width(180.dp)
                .clip(RoundedCornerShape(10.dp))
                .height(12.dp)
                .align(Alignment.TopCenter),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.secondary
        )
    }
}

