package com.app.inails.booking.admin.views.management.service.adapters

import android.content.Context
import android.support.core.view.ViewScopeOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.base.GridSpacingItemDecoration
import com.app.inails.booking.admin.databinding.DialogDetailServiceBinding
import com.app.inails.booking.admin.extention.formatPrice
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.base.AppImagesAdapter
import com.app.inails.booking.admin.views.extension.LocalImage
import com.sangcomz.fishbun.util.getDimension

class DetailServiceDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogDetailServiceBinding::inflate)
    private lateinit var adapter: AppImagesAdapter
    var onClickItemImage: ((data: Pair<List<LocalImage>, Int>) -> Unit) = {}

    init {
        binding.btClose.onClick {
            dismiss()
        }
    }

    fun show(service: IService) {
        with(binding) {
            tvSvName.text = service.name
            tvSvPrice.text = service.price.formatPrice()
            adapter = AppImagesAdapter(rcImages).apply {
                submit(service.detailImages)
                onItemImageClick = { it ->
                    onClickItemImage.invoke(
                        adapter.items().getData().map { LocalImage(it.image) } to it)
                }
            }
            rcImages.layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            rcImages.addItemDecoration(
                GridSpacingItemDecoration(
                    3,
                    context.getDimension(R.dimen.size_5),
                    false
                )
            )
        }
        super.show()
    }
}

interface DetailServiceDialogOwner : ViewScopeOwner {
    val detailServiceDialog: DetailServiceDialog
        get() = with(viewScope) {
            getOr("detailService:dialog") { DetailServiceDialog(context) }
        }
}