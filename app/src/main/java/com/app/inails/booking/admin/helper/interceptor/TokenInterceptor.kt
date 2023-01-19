package com.app.inails.booking.admin.helper.interceptor

import android.support.di.Inject
import android.support.di.ShareScope
import android.util.Log
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoTokenRequired

@Inject(ShareScope.Singleton)
class TokenInterceptor(private val userLocalSource: UserLocalSource) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val isNoTokenRequired = originRequest.tag(Invocation::class.java)?.method()
            ?.getAnnotation(NoTokenRequired::class.java) != null
        if (isNoTokenRequired) return chain.proceed(originRequest)

        var request = originRequest
        request = request.newBuilder()   .addHeader("lang", userLocalSource.getLanguage()?:"en").build()
        val token = userLocalSource.getToken()
        if (!token.isNullOrEmpty()) {
            val bearer = "Bearer $token"
            request = request.newBuilder()
                .addHeader("Authorization", bearer)
//                .addHeader("lang", userLocalSource.getLanguage()?:"en")
                .build()
        }
        return chain.proceed(request)
    }
}
