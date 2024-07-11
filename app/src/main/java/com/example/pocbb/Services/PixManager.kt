package com.example.poc_bb.Services

import android.content.Context
import android.util.Log
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.X509TrustManager

class PixManager(private val context: Context){
    suspend fun createPixKey(pixChargeRequest: PixChargeRequest, navController: NavHostController? = null) = withContext(Dispatchers.IO) {
        val sharedPreferencesUtil = SharedPreferencesUtil(context)
            val tokenManager = TokenManager(context)
            tokenManager.createAndSaveBearerToken(
                "eyJpZCI6ImY2ZDYxNDEiLCJjb2RpZ29QdWJsaWNhZG9yIjowLCJjb2RpZ29Tb2Z0d2FyZSI6MTAyNDM3LCJzZXF1ZW5jaWFsSW5zdGFsYWNhbyI6MX0",
                "eyJpZCI6IjdiNmEwODgtNWQyZS00NzA2LWI5OGItMzcxZDlkZmM5MjM2MDQwNTgzNGUtZTgiLCJjb2RpZ29QdWJsaWNhZG9yIjowLCJjb2RpZ29Tb2Z0d2FyZSI6MTAyNDM3LCJzZXF1ZW5jaWFsSW5zdGFsYWNhbyI6MSwic2VxdWVuY2lhbENyZWRlbmNpYWwiOjEsImFtYmllbnRlIjoiaG9tb2xvZ2FjYW8iLCJpYXQiOjE3MjAxODk4OTUyMjV9")
            var token = sharedPreferencesUtil.readData("token", "")
            val sslUtil = SSLUtil()
            val httpClientProvider = HttpClientProvider
            httpClientProvider.setContext(context)
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.sandbox.bb.com.br/")
                .client(httpClientProvider.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RetrofitInterface::class.java)
            var call = service.createPix(gwDevAppKey = "ef7901769533333a87ba064706e4436b", authorization = "Bearer $token", pixChargeRequest = pixChargeRequest)

            call.enqueue(object: Callback<PixChargeResponse>{
                override fun onResponse(call: Call<PixChargeResponse>, response: Response<PixChargeResponse>) {
                    if(response.isSuccessful){
                        val pixKey = response.body()?.pixCopiaECola
                        val location = response.body()?.location
                        if (pixKey == "" || pixKey == null) {
                            Log.d("PixManager", "Error on creating pix: PixKey is null or empty")
                            sharedPreferencesUtil.saveData("pixKey", "")
                            return
                        }
                        sharedPreferencesUtil.saveData("pixKey", pixKey ?: "")
                        Log.d("pixKey", response.body()?.pixCopiaECola!!)
                        navController?.navigate("qr")
                    }
                    else{
                        sharedPreferencesUtil.saveData("pixKey", "")
                        Log.d("PixManager", "Error on creating pix: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PixChargeResponse>, t: Throwable) {
                    t.printStackTrace()
                    sharedPreferencesUtil.saveData("pixKey", "")
                    Log.d("PixManager", "Error on creating pix: ${t.message}")
                }
            })
        }
    object HttpClientProvider {
        private var applicationContext: Context? = null
        private val clientInstance: OkHttpClient by lazy {
            if (applicationContext == null) {
                throw IllegalStateException("Context must be set before accessing HttpClientProvider client")
            }
            val cacheSize = 10 * 1024 * 1024 // 10 MB
            val cache = Cache(applicationContext!!.cacheDir, cacheSize.toLong())
            val sslUtil = SSLUtil()
            OkHttpClient.Builder()
                .cache(cache)
                .sslSocketFactory(sslUtil.sslContext.socketFactory, sslUtil.trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .build()
        }

        fun setContext(context: Context) {
            if (applicationContext == null) {
                applicationContext = context.applicationContext
            }
        }

        fun getClient(): OkHttpClient = clientInstance
    }
}

