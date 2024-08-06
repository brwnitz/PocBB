package com.gfxconsultoria.pocbb.Views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gfxconsultoria.pocbb.Presenter.QrPresenter
import com.gfxconsultoria.pocbb.R
import com.gfxconsultoria.pocbb.Utils.SharedPreferencesUtil
import kotlinx.coroutines.delay

@Composable
fun QrScreen(qrPresenter: QrPresenter, navController: NavController? = null) {
    var countdown by remember {
        mutableIntStateOf(SharedPreferencesUtil.getInstance().readData("pixExpiration","0")!!.toInt())
    }

    LaunchedEffect(countdown){
        while (countdown > 0){
            delay(1000L)
            countdown--
        }
    }

    val qrBitmap by qrPresenter.qrCodeBitmap
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFFF200)), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier
                .size(270.dp)
                .clip(RoundedCornerShape(15.dp))
                .shadow(elevation = 20.dp, shape = RoundedCornerShape(15.dp))
                .background(Color(0xFFE1EBF3))
                .offset(x = 0.dp, y = 0.dp), contentAlignment = Alignment.Center){
                qrBitmap?.let{
                    Image(bitmap = it.asImageBitmap(), contentDescription = "qrCode")
                }
            }
            Box(modifier = Modifier
                .size(200.dp)
                .padding(30.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(15.dp))
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Status", style = MaterialTheme.typography.bodySmall.copy(color = Color.White), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp, bottom = 16.dp))
                    Image(painter = painterResource(id = R.drawable.clock_icon), contentDescription = "icon_clock", modifier = Modifier.size(50.dp))
                    Text(text="pendente", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Yellow), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                    Text(text="expira em... ${formateSecondsToMinutes(countdown)}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary), modifier = Modifier.padding(top = 10.dp))
                }
            }
        }
        IconButton(onClick = { navController?.navigate("main") },  modifier = Modifier
            .size(42.dp)
            .align(Alignment.TopStart)
            .offset(x = 20.dp, y = 20.dp)) {
            Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "BackArrow", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(42.dp))
        }
    }
}

fun formateSecondsToMinutes(seconds: Int): String{
    val minutes = seconds / 60
    val seconds = seconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}