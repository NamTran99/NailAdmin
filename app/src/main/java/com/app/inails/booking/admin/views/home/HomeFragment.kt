package com.app.inails.booking.admin.views.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentHomeBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.FileType
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.aboutapp.BannerIntroGuidanceRepo
import com.app.inails.booking.admin.views.booking.CustomerInfoOwner
import com.app.inails.booking.admin.views.booking.StaffInfoDialogOwner
import com.app.inails.booking.admin.views.home.adapters.AdsAdapter
import com.app.inails.booking.admin.views.home.adapters.GuidanceAdapter
import com.app.inails.booking.admin.views.widget.topbar.MainTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : BaseRefreshFragment(R.layout.fragment_home), StaffInfoDialogOwner,
    CustomerInfoOwner, TopBarOwner {
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel by viewModel<ManageStaffViewModel>()
    private lateinit var adsAdapter: AdsAdapter
    private lateinit var guidanceAdapter: GuidanceAdapter

    override fun onRefreshListener() {
        refreshView()
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            topBar.state<MainTopBarState>().setTitle(R.string.title_dashboard)
            appPermission.accessNotification { }.request()
            viewRefresh.isEnabled = false
            btnManageSalon.onClick {
                Router.open(this@HomeFragment, Routing.DetailSalon)
            }
            btnManageStaff.onClick {
                Router.open(this@HomeFragment, Routing.ManageStaff)
            }
            btnManageService.onClick {
                Router.open(this@HomeFragment, Routing.ManageService)
            }
            btnReport.onClick {
                Router.open(this@HomeFragment, Routing.ReportSale)
            }
            btnStaffListToday.onClick {
                Router.open(this@HomeFragment, Routing.StaffList)
            }
            btnManageCustomer.onClick {
                Router.open(this@HomeFragment, Routing.ManageCustomer)
            }
            btnBookingList.onClick {
                Router.open(
                    this@HomeFragment,
                    Routing.BookingFragment(Routing.BookingFragment.TypeBooking.CHECK_IN)
                )
            }
            btnApmList.onClick {
                Router.open(
                    this@HomeFragment,
                    Routing.BookingFragment(Routing.BookingFragment.TypeBooking.APPOINTMENTS)
                )
            }
            btRecruitment.onClick{
                Router.open(
                    self, Routing.MyRecruitment
                )
            }
            btSearchingWorker.onClick{
                Router.open(
                    self,
                    Routing.ListJobProfile
                )
            }
            adsAdapter = AdsAdapter(viewPagerAds).apply {
                onItemClick = {
                    if (it.content.isNotEmpty()) {
                        Router.run { redirectToWebView(WebViewArgs("", it.content, true)) }
                    } else
                        if (it.url.isNotEmpty()) {
                            Router.run { redirectToWebView(WebViewArgs(it.url)) }
                        }
                }
            }

            guidanceAdapter = GuidanceAdapter(viewPagerGuidance).apply {
                onItemClick = {
                    if (it.fileType != FileType.Video && it.content.isNotEmpty()) {
                        Router.run { redirectToWebView(WebViewArgs("", it.content, true)) }
                    } else {
                        if (it.fileType == FileType.Video && it.file.isNotEmpty()) {
                            Router.run { redirectToYouTube(it.file) }
                        }
                    }

                }
            }
            TabLayoutMediator(tabDotsAds, viewPagerAds) { _, _ ->
            }.attach()
            TabLayoutMediator(tabDotsGuidance, viewPagerGuidance) { _, _ ->
            }.attach()
        }

        with(viewModel) {
            adsResult.bind {
                adsAdapter.submit(it)
            }
            guidanceResult.bind {
                guidanceAdapter.submit(it)
            }

            refreshLoading.bind {
                binding.viewRefresh.isRefreshing = it
            }
        }
    }


    private fun refreshView() {
        viewModel.refresh()
    }

    override fun onResume() {
        refreshView()
        super.onResume()
    }
}


class ManageStaffViewModel(
    private val bannerIntroGuidanceRepo: BannerIntroGuidanceRepo,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    init {
        refresh()
    }

    fun refresh() {
        getAdsList()
        getGuidanceList()
    }

    val adsResult = bannerIntroGuidanceRepo.adsResults
    val guidanceResult = bannerIntroGuidanceRepo.guideResults

    private fun getAdsList() = launch(refreshLoading, error) {
        bannerIntroGuidanceRepo.getAdsList()
    }

    private fun getGuidanceList() = launch(refreshLoading, error) {
        bannerIntroGuidanceRepo.getGuideList()
    }
}