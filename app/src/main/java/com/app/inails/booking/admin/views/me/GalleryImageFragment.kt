package com.app.inails.booking.admin.views.me

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentGalleryImageBinding
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.extention.setDivider
import com.app.inails.booking.admin.model.response.AppImage

import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.me.adapters.GalleryImagePagerAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.material.tabs.TabLayoutMediator


class GalleryImageFragment : BaseFragment(R.layout.fragment_gallery_image), TopBarOwner {
    val viewModel by viewModel<GalleryVM>()
    val binding by viewBinding(FragmentGalleryImageBinding::bind)
    val args by lazy { argument<Routing.GallerySalon>() }

    private val listBeforeImages: MutableList<AppImage>
        get() = args.listBeforeImages

    private val listAfterImages: MutableList<AppImage>
        get() = args.listAfterImages
    private lateinit var galleryImageAdapter: GalleryImagePagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            topBar.setState(
                SimpleTopBarState(
                    R.string.salon_s_gallery,
                    onBackClick = {
                        activity?.onBackPressed()
                    },
                )
            )
            galleryImageAdapter = GalleryImagePagerAdapter(vpLayoutImages).apply {
                onClickDeleteImage = {
                    viewModel.deleteSalonGallery(it)
                }
            }
            TabLayoutMediator(tabLayout, vpLayoutImages) { tab, position ->
                when (position) {
                    0 -> tab.text = requireContext().getString(
                        R.string.gallery_before_format,
                        args.listBeforeImages.size.toString()
                    )
                    else -> tab.text = requireContext().getString(
                        R.string.gallery_after_format,
                        args.listAfterImages.size.toString()
                    )
                }
            }.attach()
            tabLayout.setDivider()
            vpLayoutImages.isUserInputEnabled = false
            galleryImageAdapter.submit(listOf(args.listBeforeImages, args.listAfterImages))
        }
        viewModel.apply {
            result.bind {
                listAfterImages.remove(currentDeleteImage).apply {
                    if (this) galleryImageAdapter.updateList(args.listAfterImages, 1)
                }
                listBeforeImages.remove(currentDeleteImage).apply {
                    if (this) galleryImageAdapter.updateList(args.listBeforeImages, 0)
                }

                success(R.string.delete_image_success)
            }
        }

    }
}


class GalleryVM(
    private val deleteSalonGalleryRepo: DeleteSalonGalleryRepo,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    var currentDeleteImage = AppImage()
    val result = deleteSalonGalleryRepo.result

    fun deleteSalonGallery(image: AppImage) = launch(null, error) {
        currentDeleteImage = image
        deleteSalonGalleryRepo.invoke("[${image.id}]")
    }
}

@Inject(ShareScope.Fragment)
class DeleteSalonGalleryRepo(
    private val meApi: MeApi,
) {
    val result = SingleLiveEvent<Any>()
    suspend operator fun invoke(listImageID: String) {
        result.post(
            meApi.deleteSalonGallery(listImageID).await()
        )
    }
}

