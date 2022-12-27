package com.app.inails.booking.admin.views.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.core.extensions.block
import android.support.di.inject
import android.util.AttributeSet
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ViewEnterPhoneBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.setContentView
import com.app.inails.booking.admin.extention.vibrate
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.functional.UsPhoneNumberFormatter
import java.lang.ref.WeakReference

class PhoneNumberView : LinearLayoutCompat {

    private val viewBinding by lazy { ViewEnterPhoneBinding.bind(this) }
    var onOkClickListener: ((String) -> Unit)? = null
    private val textFormatter by inject<TextFormatter>()

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        setContentView(R.layout.view_enter_phone)
        setupViews()
    }

    private fun setupViews() {
        setBackgroundResource(R.drawable.box_white_radius)
        with(viewBinding) {
            val addLineNumberFormatter = UsPhoneNumberFormatter(WeakReference(edtPhoneNumber))
            edtPhoneNumber.run {
                addTextChangedListener(addLineNumberFormatter)
                isFocusableInTouchMode = true
            }
            edtPhoneNumber.showSoftInputOnFocus = false
            btnOne.setOnClickListener { add("1") }
            btnTwo.setOnClickListener { add("2") }
            btnThree.setOnClickListener { add("3") }
            btnFour.setOnClickListener { add("4") }
            btnFive.setOnClickListener { add("5") }
            btnSix.setOnClickListener { add("6") }
            btnSeven.setOnClickListener { add("7") }
            btnEight.setOnClickListener { add("8") }
            btnNine.setOnClickListener { add("9") }
            btnZero.setOnClickListener { add("0") }
            btnDelete.setOnClickListener { delete() }
            btnDelete.setOnLongClickListener {
                delete(true)
                return@setOnLongClickListener true
            }
            btnOk.onClick { validate() }
        }
    }

    private fun validate() {
        val origin = viewBinding.edtPhoneNumber.text.toString().trim()
        if (origin.trim().length < 14)
            Toast.makeText(
                context,
                "Please enter the correct phone number format!",
                Toast.LENGTH_SHORT
            ).show()
        else
            onOkClickListener?.invoke(textFormatter.formatPhoneNumber(viewBinding.edtPhoneNumber.text.toString()))
    }

    @SuppressLint("SetTextI18n", "ServiceCast")
    private fun add(value: String) = block(viewBinding) {
        context?.vibrate()
        val origin = edtPhoneNumber.text.toString().trim()
        edtPhoneNumber.setText("$origin$value")
    }

    private fun delete(clearAll: Boolean = false) = block(viewBinding) {
        context?.vibrate(70)
        if (clearAll)
            edtPhoneNumber.setText("")
        else {
            val origin = edtPhoneNumber.text.toString().trim()
            if (origin.isEmpty()) return@block
            if (origin.length == 1) edtPhoneNumber.setText("")
            edtPhoneNumber.setText(origin.substring(0, origin.length - 1))
        }
    }

    fun clear(){
        viewBinding.edtPhoneNumber.setText("")
    }
}