package com.app.inails.booking.admin.views.splash

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.viewmodel.viewModel
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.navigate.Route
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity(R.layout.activity_splash) {
    private val viewModel by viewModel<SplashViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(3))
            withContext(Dispatchers.Main) {
                if (viewModel.user != null) {
                    Route.run { redirectToMain() }
                } else {
                    Route.run { redirectToLogin() }
                }
            }
        }
    }
}

class SplashViewModel(
    private val userLocalSource: UserLocalSource
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val user = userLocalSource.getUserDto()

}