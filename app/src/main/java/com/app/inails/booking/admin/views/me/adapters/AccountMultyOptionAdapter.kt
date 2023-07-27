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
import com.app.inails.booking.admin.databinding.LayoutDetailManiBinding
import com.app.inails.booking.admin.databinding.LayoutDetailSalonBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.response.client.UserClientDTO
import com.app.inails.booking.admin.model.response.client.UserOwnerDTO


class AccountMultyOptionAdapter(view: RecyclerView, val appMode: UserLocalSource.AppMode?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init{
        view.adapter = this
    }
    var detailUser: UserOwnerDTO?=null
    var detailMani: UserClientDTO?=null
    var listOption: List<AccountOption> = listOf()
    var onClickLogOut: (() -> Unit) = {}
    var onClickChangeName: (() -> Unit) = {}

     fun updateDetailSalon(user: UserOwnerDTO?) {
         detailUser = user
         notifyItemChanged(DETAIL_SALON_TYPE)
    }

    fun updateDetailMani(user: UserClientDTO?) {
        detailMani = user
        notifyItemChanged(DETAIL_SALON_TYPE)
    }

    fun updateOption(listOption: List<AccountOption>){
        this.listOption = listOption
        notifyItemRangeInserted(OPTION_TYPE, listOption.size)
    }

    companion object{
        const val DETAIL_SALON_TYPE = 0
        const val DETAIL_MANI_TYPE = 2
        const val OPTION_TYPE= 1
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> if(appMode == UserLocalSource.AppMode.Owner) DETAIL_SALON_TYPE else DETAIL_MANI_TYPE
            else -> OPTION_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            DETAIL_SALON_TYPE -> DetailSalonVH(parent.bindingOf(LayoutDetailSalonBinding::inflate))
            DETAIL_MANI_TYPE ->DetailManiVH(parent.bindingOf(LayoutDetailManiBinding::inflate))
            else ->OptionVM(parent.bindingOf(LayoutAccountOptionBinding::inflate))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(position){
            DETAIL_SALON_TYPE -> if(appMode == UserLocalSource.AppMode.Owner) (holder as DetailSalonVH).bind(detailUser) else  (holder as DetailManiVH).bind(detailMani)
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
        fun bind(user: UserOwnerDTO?) {
            val context = binding.root.context
            binding.apply {
                tvAccountName.text = context.getString(R.string.welcome_account, user?.admin?.name)
                tvSalonName.text = user?.admin?.salon?.name
                tvSalonJoinDate.text = user?.admin?.salon?.created_at?.toJoinedDate()
                btnLogOut.onClick{
                    onClickLogOut.invoke()
                }
                btChangeName.onClick{
                    onClickChangeName.invoke()
                }
                (user?.admin?.is_approve == 0).show(tvApproveAccount)
            }
        }
    }

    internal inner class DetailManiVH(view: LayoutDetailManiBinding) : AccountMultyOptionAdapter.BaseViewHolder<LayoutDetailManiBinding>(view) {
        @SuppressLint("SetTextI18n")
        fun bind(user: UserClientDTO?) {
            val context = binding.root.context
            binding.apply {
                tvAccountName.text = context.getString(R.string.welcome_account, user?.user?.name)
                tvSalonJoinDate.text = user?.user?.createdAt?.toJoinedDate()
                btnLogOut.onClick{
                    onClickLogOut.invoke()
                }
            }
        }
    }

    internal inner class OptionVM(view: LayoutAccountOptionBinding) :  AccountMultyOptionAdapter.BaseViewHolder<LayoutAccountOptionBinding>(view) {
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