package com.app.inails.booking.admin.views.management.staff

import android.content.Context
import android.support.core.view.ViewScopeOwner
import android.view.MotionEvent
import androidx.annotation.StringRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogCreateUpdateStaffBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner


class CreateUpdateStaffDialog(context: Context) : BaseDialog(context), ConfirmDialogOwner {
    private val binding = viewBinding(DialogCreateUpdateStaffBinding::inflate)

    var onAvatarClick :((isDefault: Boolean) -> Unit) = {}
    var onClickClearMainImage: (() -> Unit) = {}

    init {
        setCancelable(false)
        binding.apply {
            etNote.scrollContentVertical()
            etStaffPhone.inputTypePhoneUS()
            btClose.onClick {
                confirmDialog.show(
                    title = context.getString(R.string.tittle_exit_update_staff),
                    message = context.getString(R.string.message_exit),
                    function = {
                        dismiss()
                    }
                )
            }

            mainImage.onClickClearImage = {
                isDeleteAvatar = 1
                onClickClearMainImage.invoke()
            }
        }
    }

    fun updateMainImage(image: String?) {
        image?.let{
            binding.apply {
                mainImage.setImageUrl(it)
            }
        }
    }

    var isDeleteAvatar: Int = 0

    fun show(
        @StringRes title: Int,
        staff: IStaff? = null,
        function: (String, String, String, String, Int) -> Unit
    ) {
        with(binding) {
            onClickClearMainImage.invoke()
            tvTitle.setText(title)
            etStaffFirstName.setText("")
            etStaffLastName.setText("")
            etStaffPhone.setText("")
            mainImage.removePhoto()
            etNote.setText("")
            isDeleteAvatar = 0

            mainImage.onClick{_ ->
                onAvatarClick.invoke(true)
            }

            staff?.let {
                etStaffFirstName.setText(it.firstName)
                etStaffLastName.setText(it.lastName)
                etNote.setText(it.description)
                if(!it.isAvatarDefault){
                    mainImage.setImageUrl(it.avatar)
                }
                mainImage.onClick{_ ->
                    onAvatarClick.invoke(it.isAvatarDefault)
                }

                etStaffPhone.setText(it.phone.formatPhoneUSCustom())
            }

            btSubmit.onClick {
                function.invoke(
                    etStaffFirstName.text.toString(),
                    etStaffLastName.text.toString(),
                    etStaffPhone.text.toString().convertPhoneToNormalFormat(),
                    etNote.text.toString(),
                    isDeleteAvatar
                )
            }
        }
        super.show()
    }
}

interface CreateUpdateStaffOwner : ViewScopeOwner {
    val createUpdateStaffDialog: CreateUpdateStaffDialog
        get() = with(viewScope) {
            getOr("staff:dialog") { CreateUpdateStaffDialog(context) }
        }
}