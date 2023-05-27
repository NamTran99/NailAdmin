package com.app.inails.booking.admin.views.me.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.support.core.view.IHolder
import android.support.core.view.RecyclerHolder
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemPictureHolderBinding
import com.app.inails.booking.admin.databinding.LayoutBtnAddPictureBinding
import com.app.inails.booking.admin.extention.findIndex
import com.app.inails.booking.admin.extention.setImageURICustom
import com.app.inails.booking.admin.model.response.AppImage



class UploadPhotoAdapterJob(val view: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ADD_IMAGE = 0
        const val TYPE_IMAGE = 1
    }

    private var mItems = ArrayList<AppImage>()

    init {
        view.adapter = this
        mItems.add(AppImage())
    }

    val items get() = mItems
    var listImage = listOf<AppImage>()

    var onAddImagesAction: (() -> Unit)? = null
    var onRemoveImageAction: ((AppImage) -> Unit)? = null
    var onItemClickListener: ((Int) -> Unit) = {}

    @SuppressLint("NotifyDataSetChanged")
    fun changePath(listImage: List<AppImage>?) {
        if (listImage.isNullOrEmpty()) return
        mItems.clear()
        this@UploadPhotoAdapterJob.listImage = listImage?: listOf()
        mItems.add(AppImage())
        mItems.addAll(listImage)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        mItems = ArrayList(mItems.dropLast(mItems.size - 1))
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
        (holder as IHolder<AppImage>).bind(items[position])
    }

    override fun getItemCount(): Int = mItems.size

    inner class AddImageViewHolder(val binding: LayoutBtnAddPictureBinding) :
        RecyclerHolder<AppImage>(binding.root) {
        override fun bind(item: AppImage) {
            binding.root.setOnClickListener {
                onAddImagesAction?.invoke()
            }
        }
    }

    inner class ImageViewHolder(val binding: ItemPictureHolderBinding) :
        RecyclerHolder<AppImage>(binding.root) {
        @RequiresApi(Build.VERSION_CODES.Q)
        @SuppressLint("CheckResult", "ResourceAsColor")
        override fun bind(item: AppImage) {
            binding.apply {
                val context = view.context
                imgHolder.setImageURICustom(item.image)
                btClose.setOnClickListener {
                    onRemoveImageAction?.invoke(item)
                    removeItem(item)
                }
                imgHolder.setOnClickListener {
                    onItemClickListener.invoke(adapterPosition)
                }
            }
        }
    }

    fun removeItem(path: AppImage) {
        val index = mItems.findIndex { it == path }
        if (index > -1) {
            mItems.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}