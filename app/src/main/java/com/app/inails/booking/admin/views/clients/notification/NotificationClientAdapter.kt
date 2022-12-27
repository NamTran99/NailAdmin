package com.app.inails.booking.admin.views.clients.notification

import android.support.core.view.bindingOf
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemNotificationBinding
import com.app.inails.booking.admin.databinding.ItemNotificationClientBinding
import com.app.inails.booking.admin.extention.colorResource
import com.app.inails.booking.admin.extention.visible
import com.app.inails.booking.admin.model.ui.INotification
import com.app.inails.booking.admin.model.ui.client.INotificationClient
import com.app.inails.booking.admin.views.widget.PageRecyclerAdapter

class NotificationClientAdapter(view: RecyclerView) :
    PageRecyclerAdapter<INotificationClient, ItemNotificationClientBinding>(view) {
    var onClickItemListener: ((INotificationClient) -> Unit)? = null
    var onClickMoreListener: ((Pair<INotificationClient, View>) -> Unit)? = null
    override fun onCreateBinding(parent: ViewGroup): ItemNotificationClientBinding {
        return parent.bindingOf(ItemNotificationClientBinding::inflate)
    }

    override fun onBindHolder(
        item: INotificationClient,
        binding: ItemNotificationClientBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            viewUnread.visible(!item.isRead)
            txtContent.text = item.content
            txtDateTime.text = item.datetime
            btnMore.setColorFilter(view.context.colorResource(item.textColor))
            itemView.setOnClickListener { onClickItemListener?.invoke(item) }
            btnMore.setOnClickListener { onClickMoreListener?.invoke(item to it) }
        }
    }
}