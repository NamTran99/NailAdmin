package com.app.inails.booking.admin.views.clients.media

import android.os.Bundle
import android.support.core.route.close
import android.support.core.route.lazyArgument
import android.support.core.view.viewBinding
import android.view.View
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentPhotoViewerBinding
import com.app.inails.booking.admin.navigate.Router.Companion.navigate
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarClientState
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class PhotoViewerFragment : BaseFragment(R.layout.fragment_photo_viewer), TopBarOwner {
    private val binding by viewBinding(FragmentPhotoViewerBinding::bind)
    private val args by lazyArgument<Routing.PhotoViewer>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(SimpleTopBarClientState(R.string.title_photo_viewer) { close() })
        binding.ivSalon.setImageUrl(args.photoUrl)
    }
}