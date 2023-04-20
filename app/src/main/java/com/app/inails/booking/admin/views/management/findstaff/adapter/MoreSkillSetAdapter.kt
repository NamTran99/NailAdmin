package com.app.inails.booking.admin.views.management.findstaff.adapter

import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.databinding.ItemMoreSkillsetBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.ISkill
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

class MoreSkillSetAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<ISkill, ItemMoreSkillsetBinding>(view) {

    var onRemoveMoreSkillServer: ((ISkill) -> Unit) = {}

    override fun onCreateBinding(parent: ViewGroup): ItemMoreSkillsetBinding {
        return parent.bindingOf(ItemMoreSkillsetBinding::inflate)
    }

    override fun onBindHolder(
        item: ISkill,
        binding: ItemMoreSkillsetBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvContent.text = item.name
            root.onClick {
                if (item.id != -1) {
                    onRemoveMoreSkillServer.invoke(item)
                }
                removeData(item)
            }
        }
    }


//    fun getImageSelected(): MutableList<Uri> =
//        items().getData().filterIndexed { index, uri -> (index != 0) }.toMutableList()
}