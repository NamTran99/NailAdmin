package com.app.inails.booking.admin.views.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.view.viewBinding
import android.support.viewmodel.viewModel
import android.util.Log
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.BuildConfig
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivitySplashBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.utils.Utils
import com.app.inails.booking.admin.views.main.MainActivity
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity(R.layout.activity_splash) {
    private val binding by viewBinding (ActivitySplashBinding::bind)
    private val viewModel by viewModel<SplashViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appointmentID =  intent.extras?.getInt(MainActivity.APPOINTMENT_ID,0)
//        Log.d("TAG", "NamTD8 onCreate() called with: savedInstanceState = $appointmentID")
        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(3))
            withContext(Dispatchers.Main) {
                Log.d("TAG", "onCreate: NamTD8 logout ${viewModel.user}")
                if (viewModel.user != null) {
                    Router.run { redirectToMain() }
                } else {
                    Router.run { redirectToLogin() }
                }
            }
        }
        binding.tvVersion.text = Utils.getDisplayBuildConfig()
    }

}

class SplashViewModel(
    private val userLocalSource: UserLocalSource
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val user = userLocalSource.getUserDto()

}