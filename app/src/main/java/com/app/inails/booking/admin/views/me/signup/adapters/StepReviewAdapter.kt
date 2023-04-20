package com.app.inails.booking.admin.views.me.signup.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.support.core.view.bindingOf
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemStepSignupBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter

data class StepSignUp(
    @StringRes val text: Int,
    var isSelected: Boolean = false,
    var isAlpha: Boolean = false,
    var isAlreadyGo: Boolean = false,
    val id: Int = 0
) {
    companion object {
        fun getListStep(): List<StepSignUp> {
            return listOf<StepSignUp>(
                StepSignUp(R.string.step_1_sign_up, id = 1),
                StepSignUp(R.string.step_2_sign_up, id = 2),
                StepSignUp(R.string.step_3_sign_up, id = 3),
                StepSignUp(R.string.step_4_sign_up, id = 4),
                StepSignUp(R.string.step_5_sign_up, id = 5),
            )
        }
    }
}

class StepReviewAdapter(view: RecyclerView) :
    SimpleRecyclerAdapter<StepSignUp, ItemStepSignupBinding>(view) {
    val selectedItems: List<Int>
        get() = if (items.isNullOrEmpty()) emptyList()
        else items?.filter { (it as ISelector).isSelector && it.id != 0 }?.map { it.id }
            ?: emptyList()

    override fun onCreateBinding(parent: ViewGroup): ItemStepSignupBinding {
        return parent.bindingOf(ItemStepSignupBinding::inflate)
    }

    init {
        submit(StepSignUp.getListStep())
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindHolder(
        item: StepSignUp,
        binding: ItemStepSignupBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            tvName.setText(item.text)
            tvNumber.text = item.id.toString()
            configSelect(this, item)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun configSelect(
        binding: ItemStepSignupBinding,
        item: StepSignUp,
    ) {
        val context = binding.root.context
        binding.apply {
            if(item.id == currentPos) {
                tvNumber.typeface = Typeface.DEFAULT_BOLD
                tvName.typeface = Typeface.DEFAULT_BOLD
                tvName.setTextColor(context.getColor( R.color.colorPrimary))
                tvNumber.setTextColor(context.getColor( R.color.colorPrimary))
                viewLine.setBackgroundColor(R.color.colorPrimary)
            }else if(item.id < currentPos){
                tvName.setTextColor(context.getColor( R.color.green))
                tvNumber.setTextColor(context.getColor( R.color.green))
                tvNumber.typeface = Typeface.DEFAULT
                tvName.typeface = Typeface.DEFAULT
            }else{
                tvNumber.typeface = Typeface.DEFAULT
                tvName.typeface = Typeface.DEFAULT
            }
//            tvName.setTextColor(context.getColor(if(item.id >= currentPos) R.color.colorPrimary else R.color.green))
//            tvNumber.setTextColor(context.getColor(if(item.id >= currentPos) R.color.colorPrimary else R.color.green))
            lvEnable.show(item.id < currentPos)
            lvDisable.show(item.id >= currentPos)

            if(item.id > currentPos){
                root.alpha = 0.4f
            }else{
                root.alpha = 1f
            }
//            viewLine.setBackgroundColor(context.getColor(if(item.id >= currentPos) R.color.colorPrimary else R.color.green))
//            if (item.id == currentPos) {
//
//                }else if(item.id > current){
//                    items!![index].isAlpha = true
//                }else{
//                    items!![index].isAlpha = false
//                    items!![index].isSelected = true
//                }
//            if (isAlpha) {
//                root.alpha = 0.4f
//                lvDisable.show()
//                lvEnable.hide()
//            } else {
//                root.alpha = 1f
//                if(isSelected){
//                    tvName.setTextColor(context.getColor(R.color.black))
//                }else{
//                    tvName.setTextColor(context.getColor(R.color.colorPrimary))
//                }
//                lvDisable.show(!isSelected)
//                lvEnable.show(isSelected)
//            }
        }
    }

    var currentPos = 0

    fun setCurrentStep(current: Int) {
        currentPos = current
            items?.forEachIndexed { index, item ->
//                if (item.id == current) {
                    items!![index].isSelected = true
//                    items!![index].isAlpha = false
//                }
//        else if(item.id > current){
//                    items!![index].isAlpha = true
//                }else{
//                    items!![index].isAlpha = false
//                    items!![index].isSelected = true
//                }
            }
        notifyDataSetChanged()
    }
}