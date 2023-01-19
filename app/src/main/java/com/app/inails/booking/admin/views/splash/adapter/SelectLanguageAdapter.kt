package com.app.inails.booking.admin.views.splash.adapter

import android.annotation.SuppressLint
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemSelectLanguageBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.Language
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class SelectLanguageAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<Language, ItemSelectLanguageBinding>(view) {
    val selectedLanguage: String
        get() = items?.find { it.isSelector }?.code ?: "en"
     var onItemClick: ((String) -> Unit) = { _ ->}

    override fun onCreateBinding(parent: ViewGroup): ItemSelectLanguageBinding {
        return parent.bindingOf(ItemSelectLanguageBinding::inflate)
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onBindHolder(
        item: Language,
        binding: ItemSelectLanguageBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvLanguage.setText(item.name)
            configSelect(this, (item as ISelector).isSelector)
            root.onClick {
                unSelectAll()
                item.isSelector = !item.isSelector
                notifyItemChanged(adapterPosition)
                onItemClick.invoke(item.code)
            }
        }
    }

    private fun configSelect(binding: ItemSelectLanguageBinding, isSelected: Boolean) {
        binding.tvLanguage.setCompoundDrawablesWithIntrinsicBounds(
            null, null,
            if (isSelected) ContextCompat.getDrawable(
                binding.root.context,
                R.drawable.ic_check
            ) else null, null
        )

    }

    @SuppressLint("NotifyDataSetChanged")
    fun unSelectAll() {
        items?.forEach {
            (it as ISelector).isSelector = false
        }
        notifyDataSetChanged()
    }

    override fun submit(newItems: List<Language>?) {
        super.submit(newItems)
    }
}