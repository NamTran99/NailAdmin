package com.app.inails.booking.admin.views.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.LayoutCircleImageWithProgressBinding
import com.app.inails.booking.admin.extention.getLoadingCircleDrawable
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.extention.show
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class LoadingCircleImage(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    private val binding =
        LayoutCircleImageWithProgressBinding.inflate(LayoutInflater.from(context), this, true)


    init {
        initView()
    }

    private fun initView() {
        with(binding){
            val progressDrawable = context.getLoadingCircleDrawable()
            progressBar.setIndeterminateDrawableTiled(progressDrawable)
        }
    }

    fun setImageUrl(link: String){
        with(binding){
            if (link.isEmpty()) {
                imgImage.setImageDrawable(context.getDrawable(R.drawable.img_logo))
                return
            }

            progressBar.show()
            Picasso.get().load(link)
                .error(R.drawable.img_logo)
                .into(imgImage, object : Callback {
                    override fun onSuccess() {
                        binding.progressBar.hide()
                    }

                    override fun onError(e: Exception) {
                        binding.progressBar.hide()
                        Log.d("TAG", "NamTD8 onError: ${e.message}")
                    }
                })
        }
    }
}

