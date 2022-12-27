package com.app.inails.booking.admin.views.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.LoadingImageBinding
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.extention.loadAttrs
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class LoadingImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private val binding by lazy {
        LoadingImageBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private var enableRemoveImage = true
        set(value) {
            field = value
            binding.btClose.show(value)
        }

    init {
        binding.apply {
            btClose.onClick{
                onClickClearImage.invoke()
                removePhoto()
            }
        }

        context.loadAttrs(attrs, R.styleable.LoadingImageView) {
            enableRemoveImage = it.getBoolean(R.styleable.LoadingImageView_custom_enable_remove_image, true)
        }
    }

    var onClickClearImage : (()->Unit) = {}

    private var url: String = ""
        set(value) {
            field = value
            if(url.isNotEmpty()){
                binding.apply {
                    lvAddImage.setBackgroundResource(R.drawable.bg_stroke_corner_f7f7f7)
                    lvAddImage.show()
                    lvNoImage.hide()
                }
            }
        }

    fun removePhoto(){
        binding.apply {
            btClose.hide()
            tvAddImage.show()
            lvAddImage.hide()
            lvNoImage.show()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setImageUrl(url: String?) {
        if (url.isNullOrBlank()) {
            binding.apply {
                progressBar.hide()
                image.setImageDrawable(context.getDrawable(R.drawable.ic_launcher))
            }
            return
        }
        this.url = url
        Picasso.get().load(url).into(binding.image, object : Callback {
            override fun onSuccess() {
                binding.apply {
                    lvAddImage.setBackgroundResource(0)
                    progressBar.hide()
                    btClose.show(enableRemoveImage)
                }
            }

            override fun onError(e: Exception?) {
                binding.apply {
                    tvAddImage.hide()
                    lvAddImage.hide()
                    lvNoImage.show()
                }
            }
        })
    }
}