package com.app.inails.booking.admin.views.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentIntroBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.extention.getAppResourceId
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.popups.PopupUserMoreOwner
import com.app.inails.booking.admin.repository.aboutapp.BannerIntroGuidanceRepo
import com.app.inails.booking.admin.views.management.staff.CreateUpdateStaffOwner
import com.app.inails.booking.admin.views.splash.adapter.IntroSliderAdapter
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.material.tabs.TabLayoutMediator

class IntroFragment : BaseFragment(R.layout.fragment_intro), TopBarOwner,
    CreateUpdateStaffOwner, PopupUserMoreOwner {
    private val binding by viewBinding(FragmentIntroBinding::bind)
    private val viewModel by viewModel<IntroVM>()

    lateinit var introSliderAdapter: IntroSliderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvSkip.onClick{
                viewModel.setFirstOpenAppAlready()
                Router.run { redirectToLogin() }
            }
            introSliderAdapter = IntroSliderAdapter(introSliderViewPager)
            TabLayoutMediator(tabLayout, introSliderViewPager, true, true) { tab, position ->

            }.attach()
            introSliderViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onPageSelected(position: Int) {
                    tvSkip.show(position != introSliderAdapter.itemCount - 1)
                    btnSkipStart.onClick {
                        if(position == introSliderAdapter.itemCount - 1){
                            viewModel.setFirstOpenAppAlready()
                            Router.run { redirectToLogin() }
                        }else{
                            introSliderViewPager.setCurrentItem(position + 1, true)
                        }
                    }
                   if(position == introSliderAdapter.itemCount - 1){
                       btnSkipStart.setCompoundDrawables(null,null,null,null)
                       btnSkipStart.setBackgroundColor(requireContext().getColor(R.color.colorPrimary))
                       btnSkipStart.setTextColor(requireContext().getColor(R.color.white))
                       btnSkipStart.background = requireContext().getDrawable(R.drawable.button_primary_corner_1)
                       btnSkipStart.text = requireContext().getString(R.string.start)
                   }else{
                       btnSkipStart.setCompoundDrawables(null,null,null,null)
                       btnSkipStart.setCompoundDrawablesWithIntrinsicBounds(null,null,requireContext().getDrawable(R.drawable.ic_arrow_right), null)
                       btnSkipStart.background = requireContext().getDrawable(R.drawable.button_f5f5f5_corner)
                       btnSkipStart.setTextColor(requireContext().getColor(R.color.black))
                       btnSkipStart.text = requireContext().getString(R.string.btn_next)
                   }
                }
            })
        }

        viewModel.apply {
            listIntro.bind {
                introSliderAdapter.submit(it)
            }
        }
    }
}

class IntroVM(
    private val userLocalSource: UserLocalSource,
    private val bannerIntroGuidanceRepo: BannerIntroGuidanceRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val listIntro = bannerIntroGuidanceRepo.introResults

    init {
        getListIntro()
    }

    fun setFirstOpenAppAlready(){
        userLocalSource.saveOpenAppAlready()
        Log.d("TAG", "setFirstOpenAppAlready:${userLocalSource.getIsFirstOpenApp()}")
    }

    fun getListIntro() = launch(refreshLoading, error) {
        bannerIntroGuidanceRepo.getIntroList()
    }
}