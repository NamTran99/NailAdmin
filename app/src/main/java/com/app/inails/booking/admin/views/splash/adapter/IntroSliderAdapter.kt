package com.app.inails.booking.admin.views.splash.adapter

import android.support.core.view.bindingOf
import android.text.Html
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.app.inails.booking.admin.databinding.ItemIntroSliderBinding
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.model.ui.IIntro
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter2
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class IntroSliderAdapter(recyclerView: ViewPager2) :
    SimpleRecyclerAdapter2<IIntro, ItemIntroSliderBinding>(recyclerView) {
    override fun onCreateBinding(parent: ViewGroup): ItemIntroSliderBinding {
       return parent.bindingOf(ItemIntroSliderBinding::inflate)
    }

    override fun onBindHolder(item: IIntro, binding: ItemIntroSliderBinding, adapterPosition: Int) {
        binding.apply {
            tvContent.text = Html.fromHtml(item.content, Html.FROM_HTML_MODE_COMPACT)
            tvTitle.text = item.title

            Picasso.get().load(item.image).into(image, object :Callback{
                override fun onSuccess() {
                    progressBar.hide()
                }

                override fun onError(e: Exception?) {
                    progressBar.hide()
                }
            })
        }
    }
}