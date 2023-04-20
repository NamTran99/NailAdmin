package com.app.inails.booking.admin.views.me

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentSelectLanguageBinding
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.MeApi
 
import com.app.inails.booking.admin.model.ui.Language
import com.app.inails.booking.admin.model.ui.getListDefaultLanguage
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.app.inails.booking.admin.views.splash.adapter.SelectLanguageAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class SelectLanguageAccountFragment : BaseFragment(R.layout.fragment_select_language), ConfirmDialogOwner, TopBarOwner {
    private val binding by viewBinding(FragmentSelectLanguageBinding::bind)
    val viewModel by viewModel<SelectLanguageVM>()
    private lateinit var adapter: SelectLanguageAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_language,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )
        binding.apply {
            adapter = SelectLanguageAdapter(rcLanguage).apply {
                submit(getListDefaultLanguage(userLocalSource.getLanguageWithDefault()))
                onItemClick = {
                    setLanguage(it, false)
                    viewModel.changeLanguage()
                    requireActivity().finish()
                }
            }
        }
    }
}

class SelectLanguageVM(
    private val changeLanguageRepo: ChangeLanguageRepo,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    fun changeLanguage() = launch(null, error) {
        changeLanguageRepo()
    }
}


@Inject(ShareScope.Fragment)
class ChangeLanguageRepo(
    private val meApi: MeApi,
    private val appCache: AppCache,
    private val userLocalSource: UserLocalSource
) {
    val result = SingleLiveEvent<Any>()
    suspend operator fun invoke() {
        result.post(
            meApi.changeLanguage(appCache.deviceToken, userLocalSource.getLanguageWithDefault()).await()
        )
    }
}
