package com.app.inails.booking.admin.views.widget

import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.SearchFilterLayoutBinding
import com.app.inails.booking.admin.extention.loadAttrs
import com.app.inails.booking.admin.extention.onSearchListener
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.extention.visible
import com.app.inails.booking.admin.model.ui.AppointmentFilterForm
import java.util.*

interface SearchFilterViewInf{
    var onLayoutFilterClick: (()-> Unit)?
    var onClickSearchAction: ((String) -> Unit)?
    fun setText(search: String)
    fun setHint(@StringRes hint: Int)
    fun showHideImgFilter(form: AppointmentFilterForm)
}

class SearchFilterView(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet), SearchFilterViewInf {

    override var onLayoutFilterClick: (()-> Unit)? = null
    override var onClickSearchAction: ((String) -> Unit)? = null
    val text: String
        get() = binding.searchView.text.toString()

    @StringRes
    private var mHintId = Resources.ID_NULL
    private var isEnableFilter = false

    private val binding by lazy {
        SearchFilterLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        context.loadAttrs(attributeSet, R.styleable.SearchFilterView) {
            mHintId = it.getResourceId(R.styleable.SearchFilterView_android_hint, Resources.ID_NULL)
            isEnableFilter = it.getBoolean(R.styleable.SearchFilterView_isEnableFilter, false)
        }

        setUpView()
        setUpListener()
    }

    override fun showHideImgFilter(form: AppointmentFilterForm){
        form.isHaveDataForFilter().show(binding.imgFilter)
    }

    override fun setText(search: String){
        if(search == binding.searchView.text.toString() && search == "") return // if remove this line will be crash
        binding.searchView.setText(search)
        binding.searchView.setSelection(search.length)
    }

    override fun setHint(@StringRes hint: Int) {
        binding.searchView.setHint(hint)
    }

    fun setOnSearchListener(
        timeDelay: Long = TIME_DELAY,
        onLoading: () -> Unit,
        onSearch: (String) -> Unit
    ) {
        binding.searchView.addTextChangedListener(object : TextWatcher {
            var timer: Timer? = null
            var handler = Handler(Looper.getMainLooper())

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                onLoading.invoke()
                if (timer != null) timer!!.cancel()
                timer = Timer()
                timer!!.schedule(object : TimerTask() {
                    override fun run() {
                        handler.post { onSearch.invoke(s.toString()) }
                    }
                }, timeDelay)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setUpView() {
        with(binding) {
            searchView.setHint(mHintId)
            lvFilter.show(isEnableFilter)
        }
    }

    private fun setUpListener() {
        with(binding){
            lvFilter.setOnClickListener {
                onLayoutFilterClick?.invoke()
            }

            searchView.doOnTextChanged { text, _, _, _ ->
                text.isNullOrEmpty().not().visible(btClear)
                if(text.isNullOrEmpty()){
                    onClickSearchAction?.invoke("")
                }
            }
            
            searchView.onSearchListener {
                onClickSearchAction?.invoke(it)
            }

            btClear.setOnClickListener {
                searchView.text?.clear()
            }
        }
    }

    companion object {
        const val DEFAULT_STRING = ""
        private const val TIME_DELAY: Long = 1000
    }
}
