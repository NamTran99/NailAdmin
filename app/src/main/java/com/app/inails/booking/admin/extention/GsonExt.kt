package com.app.inails.booking.admin.extention

import com.google.gson.Gson

inline fun <reified T> String.toObject() = Gson().fromJson(this, T::class.java)!!

fun <E> MutableList<E>.toJson() = Gson().toJson(this)!!
fun <E> E.toJson() = Gson().toJson(this)!!