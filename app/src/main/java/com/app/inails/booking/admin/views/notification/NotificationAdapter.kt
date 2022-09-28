package com.app.inails.booking.admin.views.notification

import android.support.core.view.bindingOf
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemNotificationBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.INotification
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter

class NotificationAdapter(view: RecyclerView) :
    PageRecyclerAdapter<INotification, ItemNotificationBinding>(view) {
    var onClickItemListener: ((INotification) -> Unit)? = null
    var onClickMenuListener: ((View, INotification) -> Unit)? = null
    val selectedItems: List<Int>
        get() = if (items().getData().isEmpty()) emptyList()
        else items().getData().filter { (it as ISelector).isSelector && it.id != 0 }.map { it.id }

    var isShowSelect = false
    override fun onCreateBinding(parent: ViewGroup): ItemNotificationBinding {
        return parent.bindingOf(ItemNotificationBinding::inflate)
    }

    override fun onBindHolder(
        item: INotification,
        binding: ItemNotificationBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            root.onClick {
                if (isShowSelect) {
                    (item as ISelector).isSelector = !item.isSelector
                    notifyItemChanged(adapterPosition)
                } else
                    onClickItemListener?.invoke(item)
            }

            btMore.onClick {
                onClickMenuListener?.invoke(it, item)
            }

            tvContent.text = item.body
            tvDateTime.text = item.createdDate
            if (!item.isRead) {
                TextViewCompat.setTextAppearance(tvContent, R.style.AppTheme_TextView_SemiBold)
                tvContent.drawableStart(R.drawable.circle_primary)
            } else {
                TextViewCompat.setTextAppearance(tvContent, R.style.AppTheme_TextView_Medium)
                tvContent.drawableStart()
            }

            btMore.setColorFilter(ContextCompat.getColor(view.context, item.color))
            cbSelect.show(isShowSelect)
            btMore.show(!isShowSelect)
            if (isShowSelect) {
                tvContent.setMargins(
                    R.dimen.size_0,
                    R.dimen.size_16,
                    R.dimen.size_16,
                    R.dimen.size_0
                )
                tvDateTime.setMargins(
                    R.dimen.size_0,
                    R.dimen.size_8,
                    R.dimen.size_16,
                    R.dimen.size_16
                )
            } else {
                tvContent.setMargins(
                    R.dimen.size_16,
                    R.dimen.size_16,
                    R.dimen.size_0,
                    R.dimen.size_0
                )
                tvDateTime.setMargins(
                    R.dimen.size_16,
                    R.dimen.size_8,
                    R.dimen.size_0,
                    R.dimen.size_16
                )
            }

            cbSelect.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed) {
                    (item as ISelector).isSelector = !item.isSelector
                    notifyItemChanged(adapterPosition)
                }
            }
            cbSelect.isChecked = (item as ISelector).isSelector
        }
    }

    fun updateItem(item: INotification) {
        val index = getData().findIndex { it.id == item.id }
        if (index > -1) {
            getData()[index] = item
            notifyItemChanged(index)
        }
    }

    fun removeItem(ids: MutableList<Int>) {
        val indexs = mutableListOf<Int>()
        ids.forEach { id ->
            indexs.add(getData().findIndex { it.id == id })
        }
        indexs.forEach {
            if (it > -1) {
                getData().removeAt(it)
            }
        }
        isShowSelect = false
        notifyDataSetChanged()
    }

    fun showSelect(isShow: Boolean) {
        if (!isShow) {
            items().getData().forEachIndexed { index, iNotification ->
                (items().getData()[index] as ISelector).isSelector = false
            }
        }
        isShowSelect = isShow
        notifyDataSetChanged()
    }
}