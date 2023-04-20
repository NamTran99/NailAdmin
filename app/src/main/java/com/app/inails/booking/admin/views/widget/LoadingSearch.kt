package com.app.inails.booking.admin.views.widget

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.EdtPasswordInputBinding
import com.app.inails.booking.admin.databinding.ItemLoadingSearchBinding
import com.app.inails.booking.admin.extention.loadAttrs
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.extention.visible

interface ILoadingSearch{
    fun setLoading(isShow: Boolean)
}

class LoadingSearch(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet),ILoadingSearch {

    private val binding =
        ItemLoadingSearchBinding.inflate(LayoutInflater.from(context), this, true)


    override fun setLoading(isShow: Boolean) {
        binding.progressBar.visible(isShow)
    }
}

