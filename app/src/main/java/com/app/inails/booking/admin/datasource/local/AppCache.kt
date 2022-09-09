package com.app.inails.booking.admin.datasource.local

import android.content.Context
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.persistent.Parser
import android.support.persistent.cache.Caching
import com.google.gson.Gson
import java.lang.reflect.Type

@Inject(ShareScope.Singleton)
class AppCache(context: Context) : Caching(context, ParserImpl()) {
    var email: String by string("email", "")
    var password: String by string("password", "")
    var tokenPush: String by string("token", "")
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