package com.app.inails.booking.admin.views.clients.profile

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.map
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentProfileBinding
import com.app.inails.booking.admin.databinding.FragmentProfileClientBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.extention.lock
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.factory.client.ProfileClientFactory
import com.app.inails.booking.admin.model.ui.IUser
import com.app.inails.booking.admin.model.ui.client.IUserClient
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.navigate
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarClientState
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ProfileClientFragment : BaseFragment(R.layout.fragment_profile_client), TopBarOwner {

    private val binding by viewBinding(FragmentProfileClientBinding::bind)
    private val viewModel by viewModel<ProfileVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(SimpleTopBarClientState(R.string.mn_profile) { activity?.onBackPressed() })
        with(viewModel) {
            display(userLive)
        }
        with(binding) {
            btnEditProfile.onClick { Router.run { redirectToEdit() } }
        }
    }

    private fun display(it: IUserClient?) = lock(binding) {
        txtWelcome.text = it?.welcomeName
        txtPhone.text = it?.phone
        txtDob.text = it?.dob
        txtEmail.text = it?.email
        txtAddress.text = it?.address
    }
}

class ProfileVM(profileDisplayRepo: ProfileDisplayRepository) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {
    val userLive = profileDisplayRepo.getProfileLive()
}

@Inject(ShareScope.Fragment)
class ProfileDisplayRepository(
    private val userLocalSource: UserLocalSource,
    private val profileFactory: ProfileClientFactory
) {

    fun getProfileLive(): IUserClient? {
        return profileFactory.displayProfile(userLocalSource.getUserClientDto())
    }

    fun getAllUserLive(): LiveData<Pair<Boolean, IUserClient?>> {
        return userLocalSource.getAllUserLive()
            .map { Pair(it?.first != null, profileFactory.displayProfile(it?.second)) }
    }
}
