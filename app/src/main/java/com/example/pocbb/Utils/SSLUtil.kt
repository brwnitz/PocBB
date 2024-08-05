package com.example.pocbb.Utils

import android.content.Context
import com.example.pocbb.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class SSLUtil {
    val trustAllCerts = arrayOf<X509TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    })
    val sslContext = SSLContext.getInstance("SSL").apply {
        init(null, trustAllCerts, java.security.SecureRandom())
    }

    fun createTrustManager(certificates: List<X509Certificate>): Array<TrustManager>{
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            certificates.forEachIndexed { index, certificate ->
                setCertificateEntry("ca$index", certificate)
            }
        }

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
            init(keyStore)
        }

        return trustManagerFactory.trustManagers
    }

    fun loadCertificate(context: Context, certificateResIds: List<Int>): List<X509Certificate>{
        val certificateFactory = CertificateFactory.getInstance("X.509")
        return certificateResIds.map { resId ->
            val inputStream: InputStream = context.resources.openRawResource(resId)
            val certificate = certificateFactory.generateCertificate(inputStream) as X509Certificate
            inputStream.close()
            certificate
        }
    }

    fun createUnsafeOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val sslHandshakeInterceptor = SSLHandshakeInterceptor()

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0])
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(interceptor)
            .addInterceptor(sslHandshakeInterceptor)
            .build()
    }

    fun createOkHttpClient(trustManagers: Array<TrustManager>): OkHttpClient {
        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, trustManagers, java.security.SecureRandom())
        }
        val interceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val sslHandshakeInterceptor = SSLHandshakeInterceptor()

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(interceptor)
            .addInterceptor(sslHandshakeInterceptor)
            .build()
    }
}