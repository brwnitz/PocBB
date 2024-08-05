package com.example.poc_bb.Services

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.poc_bb.Interfaces.RetrofitInterface
import com.example.poc_bb.Models.PixChargeRequest
import com.example.poc_bb.Models.PixChargeResponse
import com.example.poc_bb.Models.TokenResponse
import com.example.poc_bb.Utils.SharedPreferencesUtil
import com.example.pocbb.Utils.SSLUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.X509TrustManager

class PixManager(private val context: Context){
    suspend fun createPixKey(pixChargeRequest: PixChargeRequest, navController: NavHostController? = null, onSuccess: () -> Unit, onError: (Boolean) -> Unit, updatePix: (String) -> Unit) = withContext(Dispatchers.IO) {
        val sharedPreferencesUtil = SharedPreferencesUtil(context)
            val tokenManager = TokenManager(context)
            tokenManager.createAndSaveBearerToken(SharedPreferencesUtil.getInstance().readData("clientId",""),
                SharedPreferencesUtil.getInstance().readData("clientSecret",""))

            var token = sharedPreferencesUtil.readData("token", "")
            val sslUtil = SSLUtil()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api-pix.bb.com.br/")
                .client(sslUtil.createUnsafeOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RetrofitInterface::class.java)
            var call = service.createPix(gwDevAppKey = SharedPreferencesUtil.getInstance().readData("devKey",""), authorization = "Bearer $token", pixChargeRequest = pixChargeRequest)

            call.enqueue(object: Callback<PixChargeResponse>{
                override fun onResponse(call: Call<PixChargeResponse>, response: Response<PixChargeResponse>) {
                    if(response.isSuccessful){
                        val pixKey = response.body()?.pixCopiaECola
                        val location = response.body()?.location
                        if (pixKey == "" || pixKey == null) {
                            Log.d("PixManager", "Error on creating pix: PixKey is null or empty")
                            sharedPreferencesUtil.saveData("pixKey", "")
                            onError(true)
                            return
                        }
                        sharedPreferencesUtil.saveData("pixKey", pixKey ?: "")
                        Log.d("pixKey", response.body()?.pixCopiaECola!!)
                        updatePix(response.body()?.pixCopiaECola!!)
                        onSuccess()
                        navController?.navigate("qr")
                    }
                    else{
                        sharedPreferencesUtil.saveData("pixKey", "")
                        Log.d("PixManager", "Error on creating pix: ${response.message()}")
                        onError(true)
                    }
                }

                override fun onFailure(call: Call<PixChargeResponse>, t: Throwable) {
                    t.printStackTrace()
                    sharedPreferencesUtil.saveData("pixKey", "")
                    Log.d("PixManager", "Error on creating pix: ${t.message}")
                    onError(true)
                }
            })
        }
}

