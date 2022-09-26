package com.app.inails.booking.admin.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.SearchLayoutBinding
import com.app.inails.booking.admin.extention.loadAttrs

class SearchLayout(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    private var isOpenFilterMenu = false

    val onClickOpenFilterMenu: ((Boolean) -> Unit)? = null

    private val binding by lazy {
        SearchLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }

    var hint = DEFAULT_STRING

    init {
        context.loadAttrs(attributeSet, R.styleable.PasswordLayout) {
            hint = it.getString(R.styleable.PasswordLayout_hint) ?: DEFAULT_STRING
        }

        setupListener()
    }

    private fun setupListener() {
        with(binding) {
            lvStartParent.setOnClickListener {
                isOpenFilterMenu = !isOpenFilterMenu
                onClickOpenFilterMenu?.invoke(isOpenFilterMenu)
            }
            btClose.setOnClickListener {
                isOpenFilterMenu = !isOpenFilterMenu
                onClickOpenFilterMenu?.invoke(isOpenFilterMenu)
            }

        }
    }

    companion object {
        const val DEFAULT_STRING = ""
    }
}

