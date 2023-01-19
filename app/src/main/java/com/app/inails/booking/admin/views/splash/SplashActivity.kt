package com.app.inails.booking.admin.views.splash

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.clear
import android.support.core.route.open
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.util.Log
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.BuildConfig
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivitySplashBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.model.response.VersionDTO
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.utils.Utils
import com.app.inails.booking.admin.views.clients.ClientHomeActivity
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.app.inails.booking.admin.views.main.MainActivity
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity(R.layout.activity_splash), ConfirmDialogOwner {
    private val binding by viewBinding(ActivitySplashBinding::bind)
    private val viewModel by viewModel<SplashViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(userLocalSource.getIsFirstOpenApp() != false){
            userLocalSource.clearLanguage()
        }
        binding.tvVersion.text = Utils.getDisplayBuildConfig(this, userLocalSource.getLanguageWithDefault())

        viewModel.apply {
            versionResult.bind {
                if (!BuildConfig.VERSION_NAME.contains(it.version)) {
                    confirmDialog.show(
                        title = R.string.title_new_version_update,
                        message = R.string.text_pls_new_version_update,
                        buttonConfirm = R.string.btn_update,
                        isShowCancel = false
                    ) {
                        val uri = Uri.parse("market://details?id=$packageName")
                        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            startActivity(myAppLinkToMarket)
                        } catch (e: ActivityNotFoundException) {
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(TimeUnit.SECONDS.toMillis(3))
                        withContext(Dispatchers.Main) {
                            if (viewModel.isFirstOpenApp) {
                                Router.open(this@SplashActivity, Routing.SelectLanguage)
                                return@withContext
                            }
                            if (viewModel.userOwner == null) {
                                Router.run { redirectToLogin() }
                            } else if (!viewModel.isOwnerMode) {
                                Router.run {
                                    open<ClientHomeActivity>().clear()
                                }
                            } else if (viewModel.isOwnerMode) {
                                Router.run { redirectToMain() }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getVersion()
    }
}

class SplashViewModel(
    private val userLocalSource: UserLocalSource,
    private val checkVersionApp: CheckVersionApp
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val isFirstOpenApp = userLocalSource.getIsFirstOpenApp() ?: true
    val userOwner = userLocalSource.getUserDto()
    val language = userLocalSource.getLanguage()
    val isOwnerMode = userLocalSource.getOwnerMode()
    val versionResult = checkVersionApp.result

    fun getVersion() = launch(null, error) {
        checkVersionApp()
    }
}

@Inject(ShareScope.Fragment)
class CheckVersionApp(
    private val meApi: MeApi,
    private val bookingFactory: BookingFactory,
) {
    val result = SingleLiveEvent<VersionDTO>()
    suspend operator fun invoke() {
        result.post(
            meApi.checkVersion().await()
        )
    }
}