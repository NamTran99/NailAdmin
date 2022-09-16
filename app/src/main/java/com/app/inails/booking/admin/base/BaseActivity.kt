package com.app.inails.booking.admin.base


import android.support.core.event.WindowStatusOwner
import android.support.core.extensions.LifecycleSubscriberExt
import android.support.core.route.RouteDispatcher
import android.support.di.inject
import android.support.viewmodel.ViewModelRegistrable
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.app.AppPermissionOwner
import com.app.inails.booking.admin.datasource.remote.AppEvent
import com.app.inails.booking.admin.exception.ErrorHandler
import com.app.inails.booking.admin.exception.ErrorHandlerImpl
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.app.inails.booking.admin.views.dialog.ErrorDialogOwner
import com.app.inails.booking.admin.views.dialog.loading.LoadingDialog
import es.dmoral.toasty.Toasty

abstract class BaseActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId),
    LifecycleSubscriberExt,
    ViewModelRegistrable, RouteDispatcher,
    ErrorDialogOwner,
    AppPermissionOwner,
    ConfirmDialogOwner,
    ErrorHandler by ErrorHandlerImpl() {
    val appEvent by inject<AppEvent>()
    private val loadingDialog by lazy { LoadingDialog(this, this) }

    override fun onRegistryViewModel(viewModel: ViewModel) {
        if (viewModel is WindowStatusOwner) {
            viewModel.error.bind { handle(this, it) }
            viewModel.loading.bind(loadingDialog::show)
        }
    }

    fun toast(@StringRes res: Int) {
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
    }

    fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun success(msg: String, time: Int? = Toast.LENGTH_SHORT) {
        Toasty.success(this, msg, time!!).show()
    }

    fun success(@StringRes msg: Int, time: Int? = Toast.LENGTH_SHORT) {
        val toasty: Toast = Toasty.success(this, getString(msg), time!!)
        toasty.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 220)
        toasty.show()
    }
}