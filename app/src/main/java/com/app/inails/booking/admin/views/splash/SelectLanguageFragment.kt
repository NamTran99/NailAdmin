package com.app.inails.booking.admin.views.splash

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.core.view.viewBinding
import android.support.viewmodel.viewModel
import android.view.View
import androidx.annotation.RequiresApi
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.ActivitySelectLanguageBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.getListDefaultLanguage
import com.app.inails.booking.admin.model.ui.getListDefaultLanguage1
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.app.inails.booking.admin.views.splash.adapter.SelectLanguageAdapter
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SelectLanguageFragment : BaseFragment(R.layout.activity_select_language), ConfirmDialogOwner {
    private val binding by viewBinding(ActivitySelectLanguageBinding::bind)
    private val viewModel by viewModel<SplashViewModel>()
    private lateinit var adapter: SelectLanguageAdapter
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            userLocalSource.clearLanguage()
            adapter = SelectLanguageAdapter(rcLanguage).apply {
                submit(getListDefaultLanguage1(userLocalSource.getLanguageWithDefault()))
            }
            btNext.onClick{
                Router.open(this@SelectLanguageFragment, Routing.Intro)
                userLocalSource.setLanguage(adapter.selectedLanguage)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getVersion()
    }
}