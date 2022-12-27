package com.app.inails.booking.admin.datasource.local

import android.content.Context
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.persistent.Parser
import android.support.persistent.cache.Caching
import com.app.inails.booking.admin.model.response.client.BookingClientDTO
import com.app.inails.booking.admin.model.ui.client.IServiceClient
import com.google.gson.Gson
import java.lang.reflect.Type

@Inject(ShareScope.Singleton)
class AppCache(context: Context) : Caching(context, ParserImpl()) {
    var email: String by string("email", "")
    var password: String by string("password", "")
    var deviceToken: String by string("device_token", "")
    var token: String by string("token", "")
    var clientTokenPush: String by string("token", "")
    var bookingCurrent: BookingClientDTO? by reference(BookingClientDTO::class.java.name)
    var servicesSelected: List<IServiceClient>? by reference(IServiceClient::class.java.name)
}

class ParserImpl : Parser {
    private val gson = Gson()
    override fun <T> fromJson(string: String?, type: Class<T>): T? {
        return gson.fromJson(string, type)
    }

    override fun <T> fromJson(string: String?, type: Type): T? {
        return gson.fromJson(string, type)
    }

    override fun <T> toJson(value: T?): String {
        return gson.toJson(value)
    }
}