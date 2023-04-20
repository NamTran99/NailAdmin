package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.displaySafe1
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.factory.helper.FactoryHelper
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.BannerDTO
import com.app.inails.booking.admin.model.ui.*

@Inject(ShareScope.Singleton)
class BannerFactory(textFormatter: TextFormatter): FactoryHelper(textFormatter) {
    private fun createIntro(intro: BannerDTO): IIntro {
        return object : IIntro {
            override val content: String
                get() =  displaySafe(intro.content)
            override val title: String
                get() = displaySafe(intro.title)
            override val image: String
                get() = intro.image.safe()
            override val url: String
                get() = intro.url?:""
            override val file: String
                get() = intro.file?.replace("https://www.youtube.com/watch?v=", "")?:""
            override val fileType:FileType
                get() = when(intro.file_type){
                    "image" -> FileType.Image
                    "video" -> FileType.Video
                    else -> FileType.None
                }
            override val thumbNail: String
                get() = intro.thumbnail?:""
        }
    }

    fun createListIntro(list: List<BannerDTO>): List<IIntro> {
        return list.map(this::createIntro)
    }
}