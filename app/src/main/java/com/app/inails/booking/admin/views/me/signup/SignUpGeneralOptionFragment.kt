package com.app.inails.booking.admin.views.me.signup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.clear
import android.support.core.route.open
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.shareViewModel
import android.support.viewmodel.viewModel
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentSignUpAccountBinding
import com.app.inails.booking.admin.databinding.FragmentSignUpGeneralOptionBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.auth.LoginRepo
import com.app.inails.booking.admin.utils.TimeUtils
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.main.MainActivity
import com.app.inails.booking.admin.views.me.EditScheduleArgs
import com.app.inails.booking.admin.views.me.EditScheduleFragment
import com.app.inails.booking.admin.views.me.adapters.SalonScheduleAdapter
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoAdapter
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoForPaidAdapter
import com.app.inails.booking.admin.views.me.dialogs.SignUpSuccessDialogOwner
import com.app.inails.booking.admin.views.widget.DisplayType
import com.app.inails.booking.admin.views.widget.SalonServiceView
import com.app.inails.booking.admin.views.widget.SalonStaffView
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarAdapter
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.libraries.places.api.model.Place
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.Fishton.imageAdapter
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import kotlin.math.roundToInt

class SignUpGeneralOptionFragment : BaseFragment(R.layout.fragment_sign_up_general_option), TopBarOwner,
    MessageDialogOwner, SignUpSuccessDialogOwner {
    val binding by viewBinding(FragmentSignUpGeneralOptionBinding::bind)
    val vm by shareViewModel<SignUpVM>()

    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_sign_up,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        binding.apply {
            btnFiveStep.onClick{
                Router.open(self, Routing.SignUp5Step)
            }
            btnMethod1.onClick{
                Router.open(self, Routing.SignUpSupport)
            }
        }
    }
}

