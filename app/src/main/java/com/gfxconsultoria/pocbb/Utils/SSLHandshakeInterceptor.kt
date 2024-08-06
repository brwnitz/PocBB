package com.gfxconsultoria.pocbb.Utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import kotlin.jvm.Throws

class SSLHandshakeInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return chain.proceed(chain.request())
        } catch (e: javax.net.ssl.SSLHandshakeException) {
            Log.e("SSLHandshakeException", "SSLHandshakeException: ${e.message}")
            throw e
        }
    }
}