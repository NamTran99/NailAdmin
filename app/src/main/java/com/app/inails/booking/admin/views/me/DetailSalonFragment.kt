package com.app.inails.booking.admin.views.me

import android.net.Uri
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
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
import com.app.inails.booking.admin.databinding.FragmentProfileBinding
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.factory.SalonFactory
import com.app.inails.booking.admin.model.ui.ISalonDetail
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.views.me.adapters.HomeBannerPager
import com.app.inails.booking.admin.views.me.adapters.SalonScheduleAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner


class DetailSalonFragment : BaseFragment(R.layout.fragment_profile), TopBarOwner {
    val viewModel by viewModel<DetailSalonViewModel>()
    val binding by viewBinding(FragmentProfileBinding::bind)
    private lateinit var adapter: HomeBannerPager

    var path = ArrayList<Uri>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.mn_manage_salon,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        with(binding) {
            tabDots.setupWithViewPager(vpImage)
            btnEdit.onClick {
                Router.run { redirectToUpdateSalon() }
            }
            adapter = HomeBannerPager(binding.vpImage)
        }

        with(viewModel) {
            salonDetail.bind(::displays)
        }

        refreshView()
    }

    private fun displays(item: ISalonDetail) = with(binding) {
        txtSalonName.text = item.salonName
        viewHeader.apply {
            txtAddress.text = item.address
            txtPhone.text = item.phoneNumber
//           txtTime.text=it.phoneNumber
            txtOwner.text = item.ownerName
            txtDescription.text = item.des
            SalonScheduleAdapter(rcvSchedule).submit(item.schedules)
            adapter.items = item.images?.map { it.path }
            btnDirection.onClick { appSettings.navigateMyLocationWithGoogleMap(item.lat, item.lng) }
        }
    }

    private fun refreshView() {
        viewModel.getDetailSalon()
    }

    override fun onResume() {
        super.onResume()
        refreshView()
    }
}

class DetailSalonViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val salonDetail = profileRepository.result

    fun getDetailSalon() = launch(loading, error) {
        profileRepository()
    }
}

@Inject(ShareScope.Fragment)
class ProfileRepository(
    private val meApi: MeApi,
    private val profileFactory: SalonFactory
) {
    val result = MutableLiveData<ISalonDetail>()
    suspend operator fun invoke() {
        result.post(profileFactory.createDetail(meApi.getSalonDetail().await()))
    }
}