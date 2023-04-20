package com.app.inails.booking.admin.views.booking

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemServicePriceBinding
import com.app.inails.booking.admin.extention.formatPrice
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class ServicePriceAdapter(view: RecyclerView, val enableDeleteItem: Boolean = false, var isShowBottom: Boolean = false) :
    SimpleRecyclerAdapter<IService, ItemServicePriceBinding>(view) {

    var onClickRemoveItem: (IService) -> Unit = {}
    var onItemClick: (IService) -> Unit = {}

    override fun onCreateBinding(parent: ViewGroup): ItemServicePriceBinding {
        return parent.bindingOf(ItemServicePriceBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: IService,
        binding: ItemServicePriceBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            root.onClick{
                onItemClick.invoke(item)
            }
            if(isShowBottom){
                bottomLine.show()
                topLine.hide()
            }else{
                topLine.show(!enableDeleteItem)
                bottomLine.show(enableDeleteItem)
            }
            btClose.show(enableDeleteItem)
            btClose.onClick {
                onClickRemoveItem.invoke(item)
            }
            tvServiceName.text = item.name
            tvPrice.text = item.price.formatPrice()
        }
    }
}