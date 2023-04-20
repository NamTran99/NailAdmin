package com.app.inails.booking.admin.exception

import android.app.Activity
import android.support.core.extensions.block
import android.support.core.route.RouteDispatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.extention.cast
import com.app.inails.booking.admin.extention.showKeyboard
import com.app.inails.booking.admin.views.widget.PasswordLayout
import com.app.inails.booking.admin.views.widget.basic.CustomEditText
import com.app.inails.booking.admin.views.widget.basic.IViewCustomerErrorHandler
import com.google.android.youtube.player.internal.ab
import java.net.UnknownHostException

interface ErrorHandler {
    fun handle(activity: BaseActivity, error: Throwable)
    fun handle(fragment: BaseFragment, error: Throwable)
}

class ErrorHandlerImpl : ErrorHandler {

    override fun handle(activity: BaseActivity, error: Throwable) = block(activity) {
        handle(activity, activity as RouteDispatcher, error)
    }

    override fun handle(fragment: BaseFragment, error: Throwable) = block(fragment) {
        val activity = fragment.activity.cast<BaseActivity>()
        activity?.let { handle(it, fragment as RouteDispatcher, error) }
            ?: error("Fragment should be part of AppActivity")
    }

    private fun handle(activity: BaseActivity, dispatcher: RouteDispatcher, error: Throwable) {
        when (error) {
            is ViewErrorCustom ->{
                if (dispatcher is BaseFragment) {
                    val view = if (dispatcher is BaseFragment) {
                        dispatcher.requireView().findViewById<View>(error.viewId)
                    } else {
                        (dispatcher as Activity).findViewById(error.viewId)
                    }
                    if(view is IViewCustomerErrorHandler){
                        view.handlerError(error.res)
                    }
                }
                return
            }
            is ViewError -> {
                val view = if (dispatcher is BaseFragment) {
                    dispatcher.requireView().findViewById<EditText>(error.viewId)
                } else {
                    (dispatcher as Activity).findViewById(error.viewId)
                }
                view.run {
                    this.error = activity.getString(error.res)
                    showKeyboard()
                }
                return
            }
            is ViewPassInputError -> {
                val view = if (dispatcher is BaseFragment) {
                    dispatcher.requireView().findViewById<PasswordLayout>(error.viewId)
                } else {
                    (dispatcher as Activity).findViewById(error.viewId)
                }.getEdtView()
                view.run {
                    this.error = activity.getString(R.string.error_blank_password)
                    requestFocus()
                }
                return
            }
            is ResourceException -> {
                activity.toast(error.resource)
            }
            is UnauthorizedException ->{
                activity.logout()
            }
            is UnknownHostException ->{
                activity.errorDialog.show(R.string.error, R.string.error_not_connect_network, R.string.retry) {
                    activity.recreate()
                }
            }
            else -> activity.errorDialog.show(error)
        }
    }

}

