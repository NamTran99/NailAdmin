package com.app.inails.booking.admin.views.me

import android.net.Uri
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
import com.app.inails.booking.admin.databinding.FragmentProfileBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.factory.SalonFactory
import com.app.inails.booking.admin.model.ui.ISalonDetail
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.booking.dialog.VoucherDetailDialogOwner
import com.app.inails.booking.admin.views.extension.LocalImage
import com.app.inails.booking.admin.views.extension.ShowZoomImageArgs
import com.app.inails.booking.admin.views.me.adapters.HomeBannerPager
import com.app.inails.booking.admin.views.me.adapters.SalonScheduleAdapter
import com.app.inails.booking.admin.views.me.adapters.VoucherAdapter
import com.app.inails.booking.admin.views.widget.topbar.ExtensionButton
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner


class DetailSalonFragment : BaseFragment(R.layout.fragment_profile), TopBarOwner,
    VoucherDetailDialogOwner {
    val viewModel by viewModel<DetailSalonViewModel>()
    val binding by viewBinding(FragmentProfileBinding::bind)
    private lateinit var adapter: HomeBannerPager
    private lateinit var voucherAdapter: VoucherAdapter

    var path = ArrayList<Uri>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.mn_manage_salon,
                extensionButton = ExtensionButton(isShow = true, onclick = {
                    Router.run { redirectToUpdateSalon() }
                }),
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        with(binding) {
            tabDots.setupWithViewPager(vpImage)
            voucherAdapter = VoucherAdapter(viewHeader.rcVoucher).apply {
                onItemCLick ={
                    voucherDetailDialog.show(it)
                }
            }
            adapter = HomeBannerPager(binding.vpImage).apply {
                onClickItem = {
                    val listImage = viewModel.salonDetail.value?.images?.map {
                        LocalImage(it.path)
                    }
                    if (!listImage.isNullOrEmpty()) {
                        Router.run { redirectToShowZoomImage(ShowZoomImageArgs(data = listImage to it)) }
                    }
                }
            }

        }

        with(viewModel) {
            salonDetail.bind(::displays)
        }

        refreshView()
    }


    private fun displays(item: ISalonDetail) = with(binding) {
        btnGallery.onClick {
            Router.navigate(
                this@DetailSalonFragment, Routing.GallerySalon(
                    item.beforeGalleryImage.toMutableList(),
                    item.afterGalleryImage.toMutableList()
                )
            )
        }
        txtSalonName.text = item.salonName
        viewHeader.apply {
            lvVoucher.show(item.vouchers.isNotEmpty())
            voucherAdapter.submit(item.vouchers)
            txtAddress.text = item.address
            txtEmail.text = item.email
            txtPhone.text = item.phoneNumber
            labelLocalTime.text = item.tzDisplay1
            txtOwner.text = item.ownerName
            txtDescription.text = item.des
            SalonScheduleAdapter(rcvSchedule).submit(item.schedules)
            adapter.items = item.images.map { it.path }
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
    private val profileFactory: SalonFactory,
    private val localSource: UserLocalSource
) {
    val result = SingleLiveEvent<ISalonDetail>()
    suspend operator fun invoke() {
        result.post(
            profileFactory.createDetail(
                meApi.getSalonDetail(localSource.getSalonID() ?: 0).await()
            )
        )
    }
}