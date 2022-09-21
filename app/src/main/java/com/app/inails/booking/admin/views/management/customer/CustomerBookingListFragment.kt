package com.app.inails.booking.admin.views.management.customer

import android.os.Bundle
import android.support.core.route.BundleArgument
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.view.View
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentCustomerBookingListBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.model.ui.ServiceImpl
import com.app.inails.booking.admin.popups.PopupServiceMoreOwner
import com.app.inails.booking.admin.views.management.customer.adapters.ManageCustomerListBookingAdapter
import com.app.inails.booking.admin.views.management.service.CreateUpdateServiceOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerListBookingArg(
    val list: List<ServiceImpl>? = listOf()
) : BundleArgument

class CustomerBookingListFragment : BaseFragment(R.layout.fragment_customer_booking_list),
    TopBarOwner,
    CreateUpdateServiceOwner, PopupServiceMoreOwner {
    private val binding by viewBinding(FragmentCustomerBookingListBinding::bind)
    private val args by lazy { argument<CustomerListBookingArg>() }
    private lateinit var mAdapter: ManageCustomerListBookingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }


    private fun setUpView() {
        topBar.setState(
            SimpleTopBarState(
                R.string.title_booking_list
            ) { activity?.onBackPressed() })

        with(binding) {
            viewRefresh.colorSchemeDefault()

            mAdapter = ManageCustomerListBookingAdapter(rvServices)
            mAdapter.submit(args.list)
        }
    }
}