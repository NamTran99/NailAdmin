package com.app.inails.booking.admin.views.splash

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
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
import com.app.inails.booking.admin.helper.firebase.FirebaseType
import com.app.inails.booking.admin.model.response.VersionDTO
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.utils.Utils
import com.app.inails.booking.admin.views.clients.ClientHomeActivity
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity(R.layout.activity_splash), ConfirmDialogOwner {
    private val binding by viewBinding(ActivitySplashBinding::bind)
    private val viewModel by viewModel<SplashViewModel>()
    private var functionNavigateDynamicLink: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (userLocalSource.getIsFirstOpenApp() != false) {
            userLocalSource.clearLanguage()
        }
        binding.tvVersion.text =
            Utils.getDisplayBuildConfig(this, userLocalSource.getLanguageWithDefault())
        //config firebase
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null

                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                val type = deepLink?.getQueryParameter("type")
                val id = deepLink?.getQueryParameter("id")
                if(type == null) return@addOnSuccessListener
                functionNavigateDynamicLink = {
                    when (type) {
                        FirebaseType.staff -> {
                            Router.open(this, Routing.DetailCandidate(id?.toInt() ?: 0, true))
                        }
                        FirebaseType.job ->{
                            Router.open(this, Routing.DetailRecruitment(id?.toInt() ?: 0, true))
                        }
                    }
                    functionNavigateDynamicLink = null
                }
            }.addOnFailureListener {
                // This lambda will be triggered when there is a failure.
                // Handle
                Log.d("TAG", "handleIncomingDeepLinks: ${it.message}")
            }

        viewModel.apply {
            versionResult.bind {
                if (!BuildConfig.versionCustom.contains(it.version)) {
                    confirmDialog.show(
                        title = R.string.title_new_version_update,
                        message = getString(R.string.text_pls_new_version_update, it.version),
                        buttonConfirm = com.esafirm.imagepicker.R.string.ef_ok,
                        isShowCancel = false
                    ) {
                        val uri = Uri.parse("market://details?id=$packageName")
                        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            startActivity(myAppLinkToMarket)
                        } catch (_: ActivityNotFoundException) {
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            if(functionNavigateDynamicLink== null){
                                delay(TimeUnit.MILLISECONDS.toMillis(500))
                                if (viewModel.isFirstOpenApp) {
                                    Router.open(this@SplashActivity, Routing.SelectLanguage)
                                    return@withContext
                                }
                                if (viewModel.appMode == UserLocalSource.AppMode.Owner || viewModel.appMode == UserLocalSource.AppMode.Manicurist) {
                                    Router.run { redirectToMain() }
                                } else if (viewModel.appMode == UserLocalSource.AppMode.Client) {
                                    Router.run {
                                        open<ClientHomeActivity>().clear()
                                    }
                                } else  {
                                    Router.run { redirectToLogin() }
                                }
                            }else{
                                functionNavigateDynamicLink?.invoke()
                            }

//                            delay(300)
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
    val appMode = userLocalSource.getAppMode()
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