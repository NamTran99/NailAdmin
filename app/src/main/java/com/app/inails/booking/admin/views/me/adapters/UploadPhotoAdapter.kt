package com.app.inails.booking.admin.views.me.adapters

import android.annotation.SuppressLint
import android.support.core.view.IHolder
import android.support.core.view.RecyclerHolder
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemPictureHolderBinding
import com.app.inails.booking.admin.databinding.LayoutBtnAddPictureBinding
import com.app.inails.booking.admin.extention.findIndex
import com.app.inails.booking.admin.model.ui.SalonImage
import com.bumptech.glide.Glide

class UploadPhotoAdapter(val view: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ADD_IMAGE = 0
        const val TYPE_IMAGE = 1
    }

    private var mItems = ArrayList<SalonImage>()

    init {
        view.adapter = this
        mItems.add(SalonImage())
    }

    val items get() = mItems

    var onAddImagesAction: (() -> Unit)? = null
    var onCloseImageAction: ((SalonImage) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun changePath(listImage: List<SalonImage>?){
        if(listImage.isNullOrEmpty()) return
        mItems.clear()
        mItems.add(SalonImage())
        mItems.addAll(listImage)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ADD_IMAGE ->
                AddImageViewHolder(parent.bindingOf(LayoutBtnAddPictureBinding::inflate))
            TYPE_IMAGE ->
                ImageViewHolder(parent.bindingOf(ItemPictureHolderBinding::inflate))
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_ADD_IMAGE else TYPE_IMAGE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as IHolder<SalonImage>).bind(items[position])
    }

    override fun getItemCount(): Int = mItems.size

    inner class AddImageViewHolder(val binding: LayoutBtnAddPictureBinding) :
        RecyclerHolder<SalonImage>(binding.root) {
        override fun bind(item: SalonImage) {
            binding.root.setOnClickListener {
                onAddImagesAction?.invoke()
            }
        }
    }

    inner class ImageViewHolder(val binding: ItemPictureHolderBinding) :
        RecyclerHolder<SalonImage>(binding.root) {
        override fun bind(item: SalonImage) {
            binding.apply {
                Glide.with(view.context)
                    .load(item.path)
                    .placeholder(R.drawable.ic_ab_back)
                    .into(imgHolder)
                btClose.setOnClickListener {
                    onCloseImageAction?.invoke(item)
                    removeItem(item)
                }
            }
        }
    }

    fun removeItem(path: SalonImage) {
        val index = mItems.findIndex { it == path }
        if (index > -1) {
            mItems.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}

