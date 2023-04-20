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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentContactBinding
import com.app.inails.booking.admin.databinding.FragmentSelectLanguageBinding
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.extention.lazyNone
import com.app.inails.booking.admin.model.response.UserDTO
 
import com.app.inails.booking.admin.model.ui.Language
import com.app.inails.booking.admin.model.ui.getListDefaultLanguage
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.app.inails.booking.admin.views.splash.adapter.SelectLanguageAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ContactFragment : BaseFragment(R.layout.fragment_contact), ConfirmDialogOwner, TopBarOwner {
    private val binding by viewBinding(FragmentContactBinding::bind)
    val viewModel by viewModel<ContactVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_contact,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        viewModel.apply {
            getContactData()
            binding.apply {
                successValue.bind{
                    tvEmail.text = it.email
                    tvFanpage.text = it.fanpage
                    tvPhone.text = it.hotline
                }
            }
        }
    }
}

class ContactVM(
    val contactRepo: ContactRepo
): ViewModel(), WindowStatusOwner by LiveDataStatusOwner(){
    val successValue = contactRepo.result

    fun getContactData()=launch(loading, error){
        contactRepo.getDefaultValue()
    }
}

data class ContactDTO(
    var email: String = "",
    var hotline: String = "",
    var fanpage: String = ""
)

@Inject(ShareScope.Fragment)
class ContactRepo(   private val meApi: MeApi){
    val result = MutableLiveData<ContactDTO>()
    suspend fun getDefaultValue(){
            result.post(
                meApi.getValueServiceDefault().await()
            )
    }
}