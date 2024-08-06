package com.gfxconsultoria.pocbb.Services

import android.content.Context
import android.util.Log
import com.gfxconsultoria.pocbb.Interfaces.RetrofitInterface
import com.gfxconsultoria.pocbb.Models.TokenResponse
import com.gfxconsultoria.pocbb.Utils.SSLUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.net.ssl.X509TrustManager

class TokenManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    fun createAndSaveBearerToken(clientId: String?, clientSecret: String?){
        val sslUtil = SSLUtil()
        val credentials = "$clientId:$clientSecret"
        val basicAuth = "Basic " + android.util.Base64.encodeToString(credentials.toByteArray(), android.util.Base64.NO_WRAP)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://oauth.bb.com.br/")
            .client(sslUtil.getSafeOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val authService = retrofit.create(RetrofitInterface::class.java)
        authService.getToken(basicAuth).enqueue(object: Callback<TokenResponse>{
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if(response.isSuccessful){
                    val token = response.body()?.access_token
                    sharedPreferences.edit().putString("token", token).apply()
                }
                else{
                    Log.d("TokenManager", "Error on getting token: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                t.printStackTrace()
                Log.d("TokenManager", "Error on getting token: ${t.message}")
            }
        })
        }
    }