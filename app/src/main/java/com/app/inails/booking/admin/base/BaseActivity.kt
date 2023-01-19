package com.app.inails.booking.admin.base


import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.support.core.event.WindowStatusOwner
import android.support.core.extensions.LifecycleSubscriberExt
import android.support.core.route.RouteDispatcher
import android.support.di.inject
import android.support.viewmodel.ViewModelRegistrable
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.app.AppSettingsOwner
import com.app.inails.booking.admin.app.ResultsLifecycleOwner
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AppEvent
import com.app.inails.booking.admin.exception.ErrorHandler
import com.app.inails.booking.admin.exception.ErrorHandlerImpl
import com.app.inails.booking.admin.notification.NotificationsManagerClient
import com.app.inails.booking.admin.views.clients.dialog.view_image.ViewImageDialogOwner
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.app.inails.booking.admin.views.dialog.ErrorDialogOwner
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.dialog.NotificationDialogOwner
import com.app.inails.booking.admin.views.dialog.loading.LoadingDialog
import com.esafirm.imagepicker.helper.LocaleManager
import es.dmoral.toasty.Toasty
import java.util.Locale

abstract class BaseActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId),
    NotificationDialogOwner,
    LifecycleSubscriberExt,
    ViewModelRegistrable, RouteDispatcher,
    ErrorDialogOwner,
    ConfirmDialogOwner,
    AppSettingsOwner,
    ResultsLifecycleOwner,
    ViewImageDialogOwner,
    MessageDialogOwner,
    ErrorHandler by ErrorHandlerImpl() {
    val appEvent by inject<AppEvent>()
    private val loadingDialog by lazy { LoadingDialog(this, this) }
    val notificationsManager by lazy { NotificationsManagerClient(this) }
    val userLocalSource by inject<UserLocalSource>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        resultLifecycle.handleActivityResult(requestCode, resultCode, data)
    }

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

    // unfocus edittext when select outside
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun restartActivity(){
        finish()
        startActivity(intent)
    }

    fun setLanguage(lan: String, isRestart: Boolean = true){
        if(userLocalSource.setLanguage(lan) && isRestart){
            restartActivity()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.updateResources(newBase, userLocalSource.getLanguage()))
    }

    override fun onResume() {
        if(this.resources.configuration.locale.toString() != userLocalSource.getLanguageWithDefault()){
            restartActivity()
        }
        super.onResume()
    }

    open fun logout(){
    }
}