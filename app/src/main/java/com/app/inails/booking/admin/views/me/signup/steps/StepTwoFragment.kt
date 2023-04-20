package com.app.inails.booking.admin.views.me.signup.steps

import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.support.core.livedata.forceRefresh
import android.support.core.view.viewBinding
import android.support.viewmodel.shareViewModel
import android.support.viewmodel.viewModel
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentSignUpAccountBinding
import com.app.inails.booking.admin.databinding.FragmentStepTwoBinding
import com.app.inails.booking.admin.extention.convertPhoneToNormalFormat
import com.app.inails.booking.admin.extention.getTextString
import com.app.inails.booking.admin.extention.inputTypePhoneUS
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.SignUpForm
import com.app.inails.booking.admin.utils.TimeUtils
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.me.dialogs.SignUpSuccessDialogOwner
import com.app.inails.booking.admin.views.me.signup.SignUpVM
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.libraries.places.api.model.Place

class StepTwoFragment : BaseFragment(R.layout.fragment_step_two), TopBarOwner,
    MessageDialogOwner, SignUpSuccessDialogOwner {
    val viewModel by shareViewModel<SignUpVM>()
    val binding by viewBinding(FragmentStepTwoBinding::bind)
    val signUpForm: SignUpForm
        get() = viewModel.salonForm

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_sign_up,
                onBackClick = {
                    confirmDialog.show(
                        title = getString(R.string.notice),
                        message = getString(R.string.message_exit),
                        function = {
                            activity?.onBackPressed()
                        }
                    )
                },
            )
        )
        binding.apply {
            edtSalonPhone.inputTypePhoneUS()
            edtSalonAddress.onClick {
                appSettings.openPlaceAutoComplete("", ::onPlaceSelected)
            }
            btnContinue.onClick{
                signUpForm.run {
                    salon_name = edtSalonName.getTextString()
                    salon_address = edtSalonAddress.getTextString()
                    salon_phone = edtSalonPhone.getTextString().convertPhoneToNormalFormat()
                    salon_email = edtSalonEmail.text.toString()
                }
                viewModel.checkValidStep2()
            }
            btnBack.onClick{
                viewModel.backStep.forceRefresh()
            }
        }
        viewModel.apply {
            timeZoneResult.bind {
                signUpForm.run {
                    salon_timezone = it.timeZoneId
                    offsetDisplay = TimeUtils.getTimeOffset(it.timeZoneId)
                    salon_tz = "UTC $offsetDisplay"
                }
            }
        }
    }

    private fun onPlaceSelected(place: Place) {
        binding.edtSalonAddress.setText(place.address.toString())
        val geocoder = Geocoder(requireContext())
        val listAddress = geocoder.getFromLocation(place.latLng.latitude, place.latLng.longitude, 1)

        viewModel.apply {
            salonForm.run {
                salon_lat = place.latLng.latitude
                salon_lng = place.latLng.longitude
                if (listAddress!!.isNotEmpty()) {
                    listAddress[0]!!.apply {
                        salon_country = this.countryCode ?: salon_country
                        salon_zipcode = this.postalCode ?: salon_zipcode
                        salon_state = this.adminArea ?: salon_state
                        salon_city = this.locality ?: this.subAdminArea ?: salon_city
                    }
                }
            }

            timeZoneForm.run {
                key = getString(R.string.google_api_key)
                location = "${place.latLng.latitude},${place.latLng.longitude}"
            }
            getTimeZone()
        }
    }

}
