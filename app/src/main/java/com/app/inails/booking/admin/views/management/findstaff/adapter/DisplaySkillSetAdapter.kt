package com.app.inails.booking.admin.views.management.findstaff.adapter

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemDisplaySkillsetBinding
import com.app.inails.booking.admin.model.ui.ISkill
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class DisplaySkillSetAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<ISkill, ItemDisplaySkillsetBinding>(view) {
    override fun onCreateBinding(parent: ViewGroup): ItemDisplaySkillsetBinding {
        return parent.bindingOf(ItemDisplaySkillsetBinding::inflate)
    }

    override fun onBindHolder(
        item: ISkill,
        binding: ItemDisplaySkillsetBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvSkillSet.text = item.name
        }
    }

}