package com.app.inails.booking.admin.views.me.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.LayoutAccountOptionBinding
import com.app.inails.booking.admin.databinding.LayoutDetailSalonBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.response.UserDTO

class AccountMultyOptionAdapter(view: RecyclerView) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init{
        view.adapter = this
    }
    var detailUser: UserDTO?=null
    var listOption: List<AccountOption> = listOf()
    var onClickLogOut: (() -> Unit) = {}

     fun updateDetailSalon(user: UserDTO?) {
         detailUser = user
         notifyItemChanged(DETAIL_SALON_TYPE)
    }

    fun updateOption(listOption: List<AccountOption>){
        this.listOption = listOption
        notifyItemRangeInserted(OPTION_TYPE, listOption.size)
    }

    companion object{
        const val DETAIL_SALON_TYPE = 0
        const val OPTION_TYPE= 1
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> DETAIL_SALON_TYPE
            else -> OPTION_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            DETAIL_SALON_TYPE -> DetailSalonVH(parent.bindingOf(LayoutDetailSalonBinding::inflate))
            else ->OptionVM(parent.bindingOf(LayoutAccountOptionBinding::inflate))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(position){
            DETAIL_SALON_TYPE -> (holder as DetailSalonVH).bind(detailUser)
            else -> (holder as OptionVM).bind(listOption[position - 1])
        }
    }

    override fun getItemCount(): Int {
        return listOption.size + 1
    }
    open class BaseViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root){
        val mContext: Context = binding.root.context
    }

    internal inner class DetailSalonVH(view: LayoutDetailSalonBinding) : BaseViewHolder<LayoutDetailSalonBinding>(view) {
        @SuppressLint("SetTextI18n")
        fun bind(user: UserDTO?) {
            val context = binding.root.context
            binding.apply {
                tvAccountName.text = context.getString(R.string.welcome_account, user?.admin?.name)
                tvSalonName.text = user?.admin?.salon?.name
                tvSalonJoinDate.text = user?.admin?.salon?.created_at?.toJoinedDate()
                btnLogOut.onClick{
                    onClickLogOut.invoke()
                }
                (user?.admin?.is_approve == 0).show(tvApproveAccount)
            }
        }
    }

    internal inner class OptionVM(view: LayoutAccountOptionBinding) :  BaseViewHolder<LayoutAccountOptionBinding>(view) {
        fun bind(accountOption: AccountOption) {
            binding.apply {
                tvName.text = mContext.getString(accountOption.title)
                root.setOnClickListener {
                    accountOption.onItemClick.invoke()
                }
            }
        }
    }
}

data class AccountOption(@StringRes val title: Int, val onItemClick:(()-> Unit))