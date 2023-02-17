package com.app.inails.booking.admin.views.clients.salon

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.post
import android.support.core.route.lazyArgument
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentSalonGalleryBinding
import com.app.inails.booking.admin.datasource.remote.clients.SalonApi
import com.app.inails.booking.admin.factory.SalonFactory
import com.app.inails.booking.admin.factory.client.SalonClientFactory
import com.app.inails.booking.admin.model.response.SalonDTO
 
import com.app.inails.booking.admin.model.ui.ISalonDetail
import com.app.inails.booking.admin.model.ui.client.ISalonDetailClient
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.app.inails.booking.admin.views.widget.ViewPager2Adapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarClientState
import com.google.android.material.tabs.TabLayout

class SalonGalleryFragment : BaseFragment(R.layout.fragment_salon_gallery), TopBarOwner {
    companion object {
        var beforeSize = 0
        var afterSize = 0
    }

    private val args by lazyArgument<Routing.SalonGallery>()
    private val binding by viewBinding(FragmentSalonGalleryBinding::bind)
    private val viewModel by viewModel<SalonGalleryVM>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(SimpleTopBarClientState(R.string.title_salon_gallery) { activity?.onBackPressed() })
        with(viewModel) {
            with(binding) {
                details.bind {
                    beforeSize = it.imagesBefore!!.size
                    afterSize = it.imagesAfter!!.size
                    Adapter(viewPager, this@SalonGalleryFragment).apply {
                        submit(DataConst.Tab.gallery(it.imagesBefore!!, it.imagesAfter!!))
                        setupPageChangeWith(binding.tabLayout)
                    }

                }
                viewPager.isUserInputEnabled = false

            }

            setID(args.id)
        }


    }

    class Adapter(viewPager: ViewPager2, private val fragment: Fragment) :
        ViewPager2Adapter(viewPager, fragment) {

        override fun getItemCount(): Int {
            return mRoute?.size ?: 0
        }

        override fun createFragment(position: Int): Fragment {
            return mRoute?.get(position) ?: SalonImageFragment(listOf())
        }

        override fun onBindTab(position: Int, tab: TabLayout.Tab) {
            if (position == 0) {
                tab.text = "Before ($beforeSize)"
            } else {
                tab.text = "After ($afterSize)"
            }
        }
    }
}


class SalonGalleryVM(
    private val salonFactory: SalonClientFactory,
    private val salonGalleryRepo: SalonGalleryRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val details = MutableLiveData<ISalonDetailClient>()
    fun setID(id: Long) = launch(loading, error) {
        details.post(salonFactory.createDetail(salonGalleryRepo(id)))
    }

}

@Inject(ShareScope.Fragment)
class SalonGalleryRepo(
    private val salonApi: SalonApi
) {
    suspend operator fun invoke(id: Long): SalonDTO {
        return salonApi.details(id).await()
    }

}
