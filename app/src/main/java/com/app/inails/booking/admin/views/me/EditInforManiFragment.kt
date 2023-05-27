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
import com.app.inails.booking.admin.databinding.FragmentAccountBinding
import com.app.inails.booking.admin.databinding.FragmentEditInforManiBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.exception.viewError
import com.app.inails.booking.admin.extention.getTextString
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.model.ui.UpdateUserPasswordForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.auth.LogoutRepo
import com.app.inails.booking.admin.views.me.adapters.AccountMultyOptionAdapter
import com.app.inails.booking.admin.views.me.adapters.AccountOption
import com.app.inails.booking.admin.views.widget.topbar.MainTopBarState
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class EditInforManiFragment : BaseFragment(R.layout.fragment_edit_infor_mani), TopBarOwner {
    val viewModel by viewModel<EditInforManiVM>()
    val binding by viewBinding(FragmentEditInforManiBinding::bind)
    private lateinit var adapter: AccountMultyOptionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_edit_infor,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )
        with(binding) {
            etName.setText(userLocalSource.getManicuristUser()?.user?.name.safe())
            btSave.onClick{
                viewModel.changeNameMani(etName.getTextString())
            }

            viewModel.successResult.bind{
                onBackPress()
                success(it)
            }
        }
    }

}

class EditInforManiVM(
   val changeNameRepo: ChangeNameRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val successResult = SingleLiveEvent<Int>()
    fun changeNameMani(name: String) = launch(loading, error){
        if(name.isBlank()){
            viewError(R.id.etName, R.string.error_blank_name)
        }
        changeNameRepo.invoke(name)
        successResult.post(R.string.success_update_mani)
    }
}

@Inject(ShareScope.Fragment)
class ChangeNameRepo(
    private val meApi: MeApi,
    private val userLocalSource: UserLocalSource
) {
    suspend operator fun invoke(name: String) {
        meApi.updateManicurist(name).await()
        userLocalSource.changeNameManicurist(name)
    }
}