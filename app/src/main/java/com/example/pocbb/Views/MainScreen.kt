package com.example.pocbb.Views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.poc_bb.Models.Calendario
import com.example.poc_bb.Models.Devedor
import com.example.poc_bb.Models.InfoAdicionais
import com.example.poc_bb.Models.PixChargeRequest
import com.example.poc_bb.Models.Valor
import com.example.poc_bb.Utils.SharedPreferencesUtil
import com.example.pocbb.Models.SplashModel
import com.example.pocbb.Models.SplashModelFactory
import com.example.pocbb.Presenter.MainPresenter
import com.example.pocbb.Presenter.QrPresenter
import com.example.pocbb.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.MvpDelegate
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MainScreen(navController: NavHostController? = null, sharedPreferencesUtil: SharedPreferencesUtil, qrPresenter: QrPresenter, pixChargeRequest: PixChargeRequest? = null){
    SharedPreferencesUtil.initialize(LocalContext.current)
    val context = LocalContext.current
    val splashViewModel: SplashModel = viewModel(factory = SplashModelFactory())
    var showSplash by remember {
        mutableStateOf(splashViewModel.showSplashScreen)
    }

    LaunchedEffect(key1 = true) {
        if (showSplash){
            delay(3000)
            splashViewModel.setSplashScreenShow()
            showSplash = false
        }
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
    var showLoading by remember{
        mutableStateOf(false)
    }
    var showError by remember {
        mutableStateOf<Boolean?>(null)
    }
    var showPassword by remember{
        mutableStateOf(false)
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
            TextFieldCustom(value = text, onValueChange = { text = it }, placeholder = "R$ 0,00", width = 320.dp, height = 40.dp, isCurrency = true, fontSize = 22.sp, alignment = Alignment.Center, textAlign = TextAlign.Center, isNumber = true, lenght = 16)
            IconButton(onClick = {
                showLoading = true
                coroutineScope.launch {
                val pixChargeRequest = PixChargeRequest(
                    valor = Valor(realToDouble(text)),
                    calendario = Calendario(sharedPreferencesUtil.readData("pixExpiration","")!!.toInt()),
                    chave = sharedPreferencesUtil.readData("pixReceive","")!!)
                    qrPresenter.createPixAndQR(pixChargeRequest, navController!!, onSuccess = {
                        showLoading = false
                    }, { error -> showLoading = false
                        showError = error})
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
        IconButton(onClick = { showPassword = true }, modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = 20.dp, y = 20.dp)) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
        }
        Image(painter = painterResource(id = R.drawable.image_3), contentDescription = "logo_bb", modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = (-20).dp, y = (10).dp)
            .size(80.dp))
    }
        if (showLoading){
            loadingDialog()
        }
        showError?.let { errorDialog(onDismissRequest = {showError = null}) }
    if (showPassword){
        passwordDialog(onDismissRequest = {showPassword = false}, navController = navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldCustom(value: String?, onValueChange: (String) -> Unit, placeholder: String, width: Dp, height: Dp, isCurrency: Boolean, fontSize: TextUnit, alignment: Alignment, textAlign: TextAlign, isNumber: Boolean = false, lenght: Int = 100, backgroundColor: Color = MaterialTheme.colorScheme.surface){
    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(text = value!!, selection = TextRange(value.length)))
    }
    BasicTextField(
        value = textFieldValueState,
        onValueChange = {
                newValue ->
                val truncatedValue = if (newValue.text.length > lenght){
                    newValue.text.take(lenght)
                } else {
                    newValue.text
                }
                if(isCurrency) {
                    val numericValue = truncatedValue.filter { it.isDigit() }
                    val number = numericValue.toLongOrNull() ?: 0L
                    val formattedValue = formatToReal(number)
                    textFieldValueState = TextFieldValue(
                        text = formattedValue,
                        selection = TextRange(formattedValue.length)
                    )
                    onValueChange(formattedValue)
                }
                else if (isNumber){
                    val numericValue = truncatedValue.filter { it.isDigit() }
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
                    .background(backgroundColor)
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

@Composable
fun loadingDialog(){

    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .width(270.dp)
                .shadow(elevation = (5.3).dp, shape = RoundedCornerShape(10.dp))
                .height(250.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
         Column(
             horizontalAlignment = Alignment.CenterHorizontally,
             verticalArrangement = Arrangement.Center
         ) {
             spinningArrow()
             Box(modifier = Modifier.height(40.dp))
             Text(
                 text = "processando...",
                 style = TextStyle(
                     fontSize = 24.4.sp,
                     fontWeight = FontWeight(800),
                     color = Color(0xFFC6C5C4),
                     )
             )
         }
        }
    }
}

@Composable
fun passwordDialog(onDismissRequest: () -> Unit, navController: NavHostController?){
    val passwordState = remember{ mutableStateOf("") }
    val errorState = remember{ mutableStateOf(false) }
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .width(270.dp)
                .shadow(elevation = (5.3).dp, shape = RoundedCornerShape(10.dp))
                .height(250.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color(0xFFE1EBF3))
                .padding(2.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, start = 17.dp, bottom = 26.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_security),
                        contentDescription = "Icon Security"
                    )
                }
                Text(
                    modifier = Modifier.padding(start = 27.dp, bottom = 23.dp),
                    text = "Insira a senha ",
                    style = TextStyle(
                        fontSize = 13.34.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF0061AA),

                        )
                )
                CustomInput(
                    labelName = "Password",
                    onValueChange = { newValue -> passwordState.value = newValue },
                    text = passwordState.value,
                    width = 200.dp,
                    height = 32.dp,
                    isNumber = true,
                    padding = PaddingValues(top = 0.dp, start = 20.dp),
                    haveLabel = false,
                    length = 6,
                    backgroundColor = animateBackgroundColor(errorState = errorState.value, onAnimationEnd = { errorState.value = false })
                )
                Box(modifier = Modifier.height(28.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 45.dp)){
                Button(
                    onClick = {
                        if (passwordState.value == "999999") {
                            navController?.navigate("config")
                        } else {
                            errorState.value = true
                        }
                    }, modifier = Modifier
                        .width(170.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .shadow(
                            elevation = 4.332592010498047.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(color = Color(0xFF0061AA))
                ) {
                    Text(
                        text = "Prosseguir ",
                        style = TextStyle(
                            fontSize = 13.34.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFFFFFFFF),
                        )
                    )
                }
            }
            }
        }
    }
}

@Composable
fun animateBackgroundColor(errorState: Boolean, onAnimationEnd: () -> Unit): Color {
    val coroutineScope = rememberCoroutineScope()
    val colorProgress = remember { Animatable(0f) }
    val startColor = Color.White
    val endColor = Color(0xFFEF5757)

    LaunchedEffect(errorState) {
        if (errorState) {
            coroutineScope.launch {
                colorProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 300)
                )
                colorProgress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 300)
                )
            }
            onAnimationEnd()
        }
    }

    return lerp(startColor, endColor, colorProgress.value)
}

fun lerp(startColor: Color, endColor: Color, fraction: Float): Color {
    return Color(
        red = (startColor.red + fraction * (endColor.red - startColor.red)),
        green = (startColor.green + fraction * (endColor.green - startColor.green)),
        blue = (startColor.blue + fraction * (endColor.blue - startColor.blue)),
        alpha = (startColor.alpha + fraction * (endColor.alpha - startColor.alpha))
    )
}

@Composable
fun errorDialog(onDismissRequest: () -> Unit){
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .width(270.dp)
                .shadow(elevation = (5.3).dp, shape = RoundedCornerShape(10.dp))
                .height(250.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(painter = painterResource(id = R.drawable.icon_error), contentDescription = "Erro")
                Box(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ops...\n Algo deu errado!",
                    style = TextStyle(
                        fontSize = 24.4.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight(800),
                        color = Color(0xFFEF5757),
                    )
                )
            }
        }
    }
}

@Composable
fun spinningArrow(){
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit){
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Box(
        modifier = Modifier
            .graphicsLayer(
                rotationZ = rotation.value
            ),
    ){
        Image(
            painter = painterResource(id = R.drawable.icon_spinarrow),
            contentDescription = "Arrow",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(60.dp)
        )
        }
}

