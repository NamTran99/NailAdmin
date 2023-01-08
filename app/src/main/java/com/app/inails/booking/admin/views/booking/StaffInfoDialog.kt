package com.app.inails.booking.admin.views.booking

import android.content.Context
import android.support.core.route.open
import android.support.core.view.ViewScopeOwner
import android.util.Log
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.DialogCustomerInfoBinding
import com.app.inails.booking.admin.databinding.DialogStaffInfoBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.extension.ShowZoomSingleImageActivity
import com.bumptech.glide.Glide.init


class StaffInfoDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogStaffInfoBinding::inflate)
    var onAvatarClick : ((path: String) -> Unit) = {}
    init {
        binding.btClose.onClick {
            dismiss()
        }
    }

    fun show(
        staff: IStaff
    ) {
        with(binding) {
            tvStaffName.text = staff.name
            tvPhone.text = staff.phone
            imgAvatar.setImageUrl(staff.avatar)
            imgAvatar.setOnClickListener {
                onAvatarClick.invoke(staff.avatar?:"")
            }
        }
        super.show()
    }

}

interface StaffInfoDialogOwner : ViewScopeOwner {
    val staffInfoDialog: StaffInfoDialog
        get() = with(viewScope) {
            getOr("staff_info:dialog") {
                StaffInfoDialog(context).apply {
                    onAvatarClick = { path ->
                        routerDispatcher?.open<ShowZoomSingleImageActivity>(Routing.ShowZoomSingleImage(path))
                    }
                }
            }
        }
}