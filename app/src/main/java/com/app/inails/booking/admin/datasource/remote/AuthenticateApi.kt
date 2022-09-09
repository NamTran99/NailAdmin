package com.app.inails.booking.admin.datasource.remote
import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.Async
import retrofit2.Retrofit
import retrofit2.http.POST

@InjectBy(MockAuthenticateApi::class, ShareScope.Singleton)
interface AuthenticateApi: Injectable {
    @POST("login")
    fun login(email: String, password: String): Async<String>
}

class AuthenticateApiImpl(private val retrofit: Retrofit) : AuthenticateApi by retrofit.create(AuthenticateApi::class.java)

class MockAuthenticateApi : AuthenticateApi {
    override fun login(email: String, password: String): Async<String> {
        return MockAsync("Token")
    }
}