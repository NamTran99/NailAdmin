package com.app.inails.booking.admin.views.management.findstaff.adapter

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemMainSkillsetBinding
import com.app.inails.booking.admin.model.ui.ISkill
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class MainSkillSetAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<ISkill, ItemMainSkillsetBinding>(view) {
    var onItemServerClick: ((ISkill) -> Unit) = { }
    override fun onCreateBinding(parent: ViewGroup): ItemMainSkillsetBinding {
        return parent.bindingOf(ItemMainSkillsetBinding::inflate)
    }

    override fun onBindHolder(
        item: ISkill,
        binding: ItemMainSkillsetBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            rdItem.isChecked = item.selected
            rdItem.text = item.name
            rdItem.setOnCheckedChangeListener { _, isChecked ->
                item.selected = isChecked
                if (item.isServer) {
                    onItemServerClick.invoke(item)
                }
            }
        }
    }

//    fun getImageSelected(): MutableList<Uri> =
//        items().getData().filterIndexed { index, uri -> (index != 0) }.toMutableList()
}