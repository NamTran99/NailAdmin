package com.app.inails.booking.admin.datasource.remote.sockets

import android.annotation.SuppressLint
import android.support.di.Inject
import android.support.di.ShareScope
import android.util.Log
import androidx.viewbinding.BuildConfig
import com.app.inails.booking.admin.app.AppConfig
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Inject(ShareScope.Fragment)
class SocketClient {

    private val TAG = SocketClient::class.java.simpleName

    var mSocket: Socket? = null
    private var mServerSocket = if(BuildConfig.DEBUG) AppConfig.serverSocketLive else AppConfig.serverSocketLive

    init {
        setupSocket()
        connectSocket()
    }

    private fun setupSocket() {
        try {
            mSocket = IO.socket(mServerSocket)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
        setupSSL()
    }

    private fun connectSocket() {
        mSocket?.on(Socket.EVENT_CONNECT) {
            Log.e(TAG, "EVENT_CONNECT")
        }?.on(Socket.EVENT_CONNECT_ERROR) {
            Log.e(TAG, "EVENT_CONNECT_ERROR")
        }?.on(Socket.EVENT_DISCONNECT) {
            Log.e(TAG, "EVENT_DISCONNECT")
        }
        mSocket?.connect()
    }

    @SuppressLint("TrustAllX509TrustManager")
    private fun setupSSL(): Socket? {
        try {
            val myHostnameVerifier = HostnameVerifier { _, _ -> true }
            val mySSLContext = SSLContext.getInstance("TLS")
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }
            })

            mySSLContext.init(null, trustAllCerts, java.security.SecureRandom())

            val okHttpClient = OkHttpClient.Builder()
                .hostnameVerifier(myHostnameVerifier)

                .sslSocketFactory(mySSLContext.socketFactory,
                    @SuppressLint("CustomX509TrustManager")
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {

                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {

                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    })
                .build()
            val options = IO.Options()
            options.callFactory = okHttpClient
            options.webSocketFactory = okHttpClient

            mSocket = IO.socket(mServerSocket, options)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        return mSocket
    }

    fun disconnect() {
        mSocket?.disconnect()
        mSocket?.off(Socket.EVENT_CONNECT)
        mSocket?.off(Socket.EVENT_CONNECT_ERROR)
    }

    fun connectIfNeed(){
        if (mSocket == null) {
            setupSocket()
            connectSocket()
        }
        if (!mSocket?.connected()!!)
            connectSocket()
    }
}