package com.app.inails.booking.admin.views.clients.salon

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.post
import android.support.core.route.lazyArgument
import android.support.core.route.open
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
import com.app.inails.booking.admin.databinding.FragmentSalonDetailBinding
import com.app.inails.booking.admin.datasource.local.dao.SalonDAO
import com.app.inails.booking.admin.datasource.remote.clients.SalonApi
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.factory.client.SalonClientFactory
import com.app.inails.booking.admin.model.response.client.SalonClientDTO
import com.app.inails.booking.admin.model.ui.client.ISalonDetailClient
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.main.MainNavigationActivity
import com.app.inails.booking.admin.views.widget.topbar.NoTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class SalonDetailClientFragment : BaseFragment(R.layout.fragment_salon_detail), TopBarOwner {
    private val args by lazyArgument<Routing.SalonDetail>()
    private val viewModel by viewModel<SalonDetailVM>()
    private val binding by viewBinding(FragmentSalonDetailBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(NoTopBarState())
        with(binding) {
            tabDots.setupWithViewPager(vpImage)
            btnBack.onClick { activity?.onBackPressed() }
            val permissionCall = appPermission.accessPhoneCall {
                val phoneNumber = viewHeader.txtPhone.text.toString()
                if (phoneNumber.isNotEmpty())
                    appSettings.callPhone(phoneNumber)
            }
            viewHeader.txtPhone.onClick { permissionCall.request() }
            btnViewFeedback.onClick { Router.run { redirectToFeedback(args.id) } }
        }
        with(viewModel) {
            details.bind(::displays)
            setID(args.id)
        }
    }

    private fun displays(item: ISalonDetailClient) = with(binding) {
        txtSalonName.text = item.salonName
        ratingBar.rating = item.rating
        viewHeader.apply {
            txtAddress.text = item.address
            txtPhone.text = item.phoneNumber
            txtOwner.text = item.ownerName
            txtDescription.text = item.des
            SalonScheduleClientAdapter(rcvSchedule).submit(item.schedules.first)
            txtBusinessHours.text = item.schedules.second
            SalonPhotoPager(vpImage).apply {
                items = item.images
                onItemClickListener = { Router.open(self, Routing.PhotoViewer(it)) }
            }
            btnDirection.onClick { appSettings.navigateMyLocationWithGoogleMap(item.lat, item.lng) }
        }
        btnPhotoGallery.onClick {
            open<MainNavigationActivity>(
                Routing.SalonGallery(args.id)
            )
        }
    }
}

class SalonDetailVM(
    private val salonDAO: SalonDAO,
    private val salonFactory: SalonClientFactory,
    private val salonDetailRepo: SalonDetailRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val details = MutableLiveData<ISalonDetailClient>()
    fun setID(id: Long) = launch(loading, error) {
        val salon = salonDAO.find(id)
        if (salon == null) {
            details.post(salonFactory.createDetail(salonDetailRepo(id)))
        } else
            details.post(salonFactory.createDetail(salon))
    }

}

@Inject(ShareScope.Fragment)
class SalonDetailRepo(
    private val salonApi: SalonApi,
    private val salonDAO: SalonDAO
) {
    suspend operator fun invoke(id: Long): SalonClientDTO {
        val rs = salonApi.details(id).await()
        salonDAO.save(rs)
        return rs
    }

}
