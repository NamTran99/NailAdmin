package com.app.inails.booking.admin.views.home.adapters

import android.annotation.SuppressLint
import android.os.Handler
import android.support.core.view.bindingOf
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.databinding.ItemGuidanceSliderBinding
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.FileType
import com.app.inails.booking.admin.model.ui.IIntro
import com.app.inails.booking.admin.views.me.adapters.LoopHandler
import com.app.inails.booking.admin.views.widget.SimpleRecyclerAdapter2
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class GuidanceAdapter(recyclerView: ViewPager2) :
    SimpleRecyclerAdapter2<IIntro, ItemGuidanceSliderBinding>(recyclerView) {

    var onItemClick: ((IIntro) -> Unit) = {}

    override fun onCreateBinding(parent: ViewGroup): ItemGuidanceSliderBinding {
        return parent.bindingOf(ItemGuidanceSliderBinding::inflate)
    }

    private val mViewLooper = LoopHandler {
        val next = (view.currentItem + 1) % itemCount
        view.setCurrentItem(next, true)
    }

    override fun submit(newItems: List<IIntro>?) {
        super.submit(newItems)
        if (itemCount > 0) mViewLooper.start()
        else mViewLooper.stop()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindHolder(
        item: IIntro,
        binding: ItemGuidanceSliderBinding,
        adapterPosition: Int
    ) {
        binding.apply {
            val context = root.context
            imgYoutube.show(item.fileType == FileType.Video)
            root.setOnClickListener {
                onItemClick.invoke(item)
            }
            if (item.fileType == FileType.Video) {
                if(item.thumbNail == ""){
                    image.setImageDrawable(context.getDrawable(R.drawable.ic_default_guidance))
                }else{
                    Picasso.get().load(item.thumbNail).into(image, object : Callback {
                        override fun onSuccess() {
                            progressBar.hide()
                        }

                        override fun onError(e: Exception?) {
                            progressBar.hide()
                        }
                    })
                }
            } else {
//                image.setImageDrawable(context.getDrawable(R.drawable.ic_default_guidance))
                Picasso.get().load(item.file).into(image, object : Callback {
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
}
