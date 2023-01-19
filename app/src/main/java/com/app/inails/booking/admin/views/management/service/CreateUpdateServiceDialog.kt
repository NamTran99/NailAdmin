package com.app.inails.booking.admin.views.management.service

import android.content.Context
import android.support.core.view.ViewScopeOwner
import android.text.InputFilter
import android.view.MotionEvent
import androidx.annotation.StringRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogCreateUpdateServiceBinding
import com.app.inails.booking.admin.extention.formatAmount
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.AppImage
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.dialog.ConfirmDialogOwner
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoAdapter
import com.app.inails.booking.admin.views.widget.DecimalDigitsInputFilter
import com.google.android.youtube.player.internal.v


class CreateUpdateServiceDialog(context: Context) : BaseDialog(context), ConfirmDialogOwner {
    private val binding = viewBinding(DialogCreateUpdateServiceBinding::inflate)
    private lateinit var imageAdapter: UploadPhotoAdapter

    init {
        setCancelable(false)
        binding.apply {
            imageAdapter = UploadPhotoAdapter(rvImages).apply {
                onAddImagesAction = {
                    onAddMultyImage.invoke()
                }

                onRemoveImageAction = {
                    onClickRemoveOneImage.invoke(it)
                }
            }

            btClose.onClick {
                confirmDialog.show(
                    title = context.getString(R.string.tittle_exit_update_service),
                    message = context.getString(R.string.message_exit),
                    function = {
                        dismiss()
                    }
                )
            }

            mainImage.onClickClearImage = {
                onClickClearMainImage.invoke()
            }
            etPrice.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(4, 2))
        }

    }

    fun updateMoreImage(list: ArrayList<AppImage>) {
        imageAdapter.changePath(list)
    }

    fun updateMainImage(image: AppImage?) {
        image?.let{
            binding.apply {
                mainImage.setImageUrl(it.path)
            }
        }
    }

    var onAddOneImage: (() -> Unit) = {}
    var onClickClearMainImage: (() -> Unit) = {}
    var onAddMultyImage: (() ->Unit) = {}
    var onClickRemoveOneImage: ((AppImage) ->Unit) = {}

    fun show(
        @StringRes title: Int,
        service: IService? = null,
        function: (String, String) -> Unit
    ) {
        with(binding) {
            mainImage.removePhoto()
            imageAdapter.clear()
            tvTitle.setText(title)
            service?.let {
                etServiceName.setText(it.name)
                etPrice.setText(it.price.formatAmount())
                it.avatar?.let{ image ->
                    mainImage.setImageUrl(image)
                }
                it.moreImage.let{ images ->
                    imageAdapter.changePath(images)
                }
            } ?: run {
                etServiceName.text.clear()
                etPrice.text.clear()
            }
            mainImage.onClick {
                onAddOneImage.invoke()
            }
            btSubmit.onClick {
                function.invoke(
                    etServiceName.text.toString(),
                    etPrice.text.toString(),
                )
            }
        }
        super.show()
    }
}

interface CreateUpdateServiceOwner : ViewScopeOwner {
    val createUpdateServiceDialog: CreateUpdateServiceDialog
        get() = with(viewScope) {
            getOr("service:dialog") { CreateUpdateServiceDialog(context) }
        }
}