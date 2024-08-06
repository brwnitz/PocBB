package com.gfxconsultoria.pocbb.Utils

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class SSLUtil {

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

    fun concatenateTrustManagers(trustManagers1: Array<TrustManager>, trustManagers2: Array<TrustManager>): Array<TrustManager> {
        val certificates = mutableListOf<X509Certificate>()

        // Extract certificates from the first TrustManager
        trustManagers1.forEach { trustManager ->
            if (trustManager is X509TrustManager) {
                certificates.addAll(trustManager.acceptedIssuers)
            }
        }

        // Extract certificates from the second TrustManager
        trustManagers2.forEach { trustManager ->
            if (trustManager is X509TrustManager) {
                certificates.addAll(trustManager.acceptedIssuers)
            }
        }

        // Create a new TrustManager with the combined certificates
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

    fun getSafeOkHttpClient(context: Context): OkHttpClient {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
            )

            val rootCert = loadCACertificate(context, com.gfxconsultoria.pocbb.R.raw.root_cert)
            val intermediateCert = loadCACertificate(context, com.gfxconsultoria.pocbb.R.raw.intermediate_cert)
            val enterpriseCert = loadCACertificate(context, com.gfxconsultoria.pocbb.R.raw.enterprise_cert)
            val bbcert = loadCACertificate(context, com.gfxconsultoria.pocbb.R.raw.api_webhook_bb_com_br)
            val rootCertBb = loadCACertificate(context, com.gfxconsultoria.pocbb.R.raw.autoridade_certificadora_raiz_brasileira_v10)
            val solutiCert = loadCACertificate(context, com.gfxconsultoria.pocbb.R.raw.ac_soluti_ssl_ev_g4)

            val trustManagers = createTrustManager(listOf(rootCert, intermediateCert, enterpriseCert, bbcert, rootCertBb, solutiCert))

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustManagers, SecureRandom())

            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustManagers[0] as X509TrustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
            builder.connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            builder.readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            builder.writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            builder.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })

            val okHttpClient: OkHttpClient = builder.build()
            return okHttpClient
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun newTrustManager(caCertificate: X509Certificate): Array<TrustManager>{
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            setCertificateEntry("ca", caCertificate)
        }

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
            init(keyStore)
        }
        return trustManagerFactory.trustManagers
    }

    fun loadCACertificate(context: Context, resId: Int): X509Certificate{
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val inputStream: InputStream = context.resources.openRawResource(resId)
        val certificate = certificateFactory.generateCertificate(inputStream) as X509Certificate
        inputStream.close()
        return certificate
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