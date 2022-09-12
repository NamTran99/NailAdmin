package com.app.inails.booking.admin.base

import android.support.core.event.WindowStatusOwner
import android.support.core.extensions.LifecycleSubscriberExt
import android.support.core.route.ActivityResultRegister
import android.support.core.route.RouteDispatcher
import android.support.viewmodel.ViewModelRegistrable
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.app.AppPermissionOwner
import com.app.inails.booking.admin.exception.ErrorHandler
import com.app.inails.booking.admin.exception.ErrorHandlerImpl
import com.app.inails.booking.admin.functional.NotSupportable
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.app.inails.booking.admin.views.dialog.loading.LoadingDialog

abstract class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId),
    RouteDispatcher,
    LifecycleSubscriberExt,
    ActivityResultRegister,
    ViewModelRegistrable,
    ConfirmDialogOwner,
    AppPermissionOwner,
    NotSupportable,
    ErrorHandler by ErrorHandlerImpl() {
    val self get() = this
    val appActivity get() = activity as BaseActivity
    private val loadingDialog by lazy { LoadingDialog(requireContext(), this) }

    override fun onRegistryViewModel(viewModel: ViewModel) {
        if (viewModel is WindowStatusOwner) {
            viewModel.error.bind { handle(this, it) }
            viewModel.loading.bind(loadingDialog::show)
        }
    }

    fun toast(@StringRes res: Int) = appActivity.toast(res)

    fun toast(text: String) = appActivity.toast(text)

    fun success(msg: String, time: Int? = Toast.LENGTH_SHORT) {
        appActivity.success(msg, time)
    }

    fun success(@StringRes msg: Int, time: Int? = Toast.LENGTH_SHORT) {
        appActivity.success(msg, time)
    }
}