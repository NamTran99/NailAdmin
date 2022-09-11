package com.app.inails.booking.admin.widgets

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.EdtPasswordInputBinding
import com.app.inails.booking.admin.extention.loadAttrs

class PasswordLayout(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    val text : String
        get() = binding.edtPassword.text.toString()

    private val binding by lazy {
        EdtPasswordInputBinding.inflate(LayoutInflater.from(context), this, true)
    }

    var hint = DEFAULT_STRING

    init {
        context.loadAttrs(attributeSet, R.styleable.PasswordLayout) {
            hint = it.getString(R.styleable.PasswordLayout_hint) ?: DEFAULT_STRING
        }

        setupListener()
        binding.edtPassword.hint = hint
    }

    private fun setupListener() {
        with(binding) {
            txtPassActionShow.setOnClickListener {
                isActivated = !isActivated
                txtPassActionShow.text = context.getString( if(isActivated) R.string.hide  else R.string.show)
                edtPassword
                    .showPassword(isActivated)
                    .seekCursorToLast()
            }
        }
    }

    companion object {
        const val DEFAULT_STRING = ""
    }
}

private fun EditText.seekCursorToLast() {
    setSelection(length())
}

private fun EditText.showPassword(activated: Boolean): EditText {
    transformationMethod = if (!activated) PasswordTransformationMethod.getInstance()
    else HideReturnsTransformationMethod.getInstance()
    return this
}
