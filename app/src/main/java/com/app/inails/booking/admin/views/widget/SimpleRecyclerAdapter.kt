package com.app.inails.booking.admin.views.widget

import android.support.core.view.RecyclerAdapter
import android.support.core.view.RecyclerHolder
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class SimpleRecyclerAdapter<T, V : ViewBinding>(protected val view: RecyclerView) :
    RecyclerAdapter<T>() {
    init {
        view.adapter = this
    }

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding: V = onCreateBinding(parent)
        return object : RecyclerHolder<T>(binding) {
            override fun bind(item: T) {
                super.bind(item)
                onBindHolder(item, binding, adapterPosition)
            }
        }
    }

    protected abstract fun onCreateBinding(parent: ViewGroup): V

    protected abstract fun onBindHolder(item: T, binding: V, adapterPosition: Int)
}