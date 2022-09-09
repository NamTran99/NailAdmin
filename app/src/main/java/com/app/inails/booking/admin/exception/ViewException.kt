package com.app.inails.booking.admin.exception

import androidx.annotation.IdRes
import androidx.annotation.StringRes

class ViewError(val viewId: Int, val res: Int) : Throwable()

fun viewError(@IdRes viewId: Int, @StringRes res: Int): Nothing = throw ViewError(viewId, res)

class ResourceException(val resource: Int) : Throwable()

fun resourceError(@StringRes res: Int): Nothing = throw ResourceException(res)
