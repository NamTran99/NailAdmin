package com.app.inails.booking.admin.app

import android.app.Application
import android.support.di.module
import androidx.viewbinding.BuildConfig
import com.app.inails.booking.admin.helper.factory.TLSSocketFactory
import com.app.inails.booking.admin.helper.interceptor.Logger
import com.app.inails.booking.admin.helper.interceptor.LoggingInterceptor
import com.app.inails.booking.admin.helper.interceptor.TokenInterceptor
import com.app.inails.booking.admin.helper.network.ApiAsyncAdapterFactory
import com.app.inails.booking.admin.helper.network.DefaultApiErrorHandler
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


val appModule = module {
    modules(serializeModule, networkModule, apiModule)
}

val serializeModule = module {
    single { Gson() }
    single<Converter.Factory> {
        GsonConverterFactory.create(
            GsonBuilder()
                .create()
        )
    }
}

val networkModule = module {

    single {
        LoggingInterceptor.Builder()
            .loggable(BuildConfig.DEBUG)
            .setLevel(Logger.Level.BASIC)
            .log(Platform.INFO)
            .request("Request")
            .response("Response")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
    }

    single {
        val application: Application = get()
        val cacheDir = File(application.cacheDir, UUID.randomUUID().toString())
        val cache = Cache(cacheDir, 10485760L) // 10mb
        val tlsSocketFactory = TLSSocketFactory()
        OkHttpClient.Builder()
            .sslSocketFactory(tlsSocketFactory, tlsSocketFactory.systemDefaultTrustManager())
            .cache(cache)
            .addInterceptor(get<LoggingInterceptor.Builder>().build())
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    factory<Retrofit.Builder> {
        Retrofit.Builder()
            .addConverterFactory(get())
            .client(get())
    }
}

val apiModule = module {
    single {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        get<Retrofit.Builder>()
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(get<TokenInterceptor>())
                    .addInterceptor(interceptor)
//                    .addInterceptor(get<LoggingInterceptor.Builder>().build())
                    .build()
            )
            .baseUrl(AppConfig.endpoint)
            .addCallAdapterFactory(ApiAsyncAdapterFactory(DefaultApiErrorHandler()))
            .build()
    }
}